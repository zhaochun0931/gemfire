package com.mycompany.app;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.TombstoneService;
import org.apache.geode.internal.cache.versions.RegionVersionHolder;
import org.apache.geode.internal.cache.versions.RegionVersionVector;
import org.apache.geode.internal.cache.versions.VersionSource;
import org.apache.geode.logging.internal.log4j.api.LogService;

/**
 * A function to force tombstone GC in order to clear out RVV exceptions.
 *
 * This function can be called from gfsh as:
 *
 * <pre>
 *   execute function --id=tombstone-gc --arguments=REGION_NAME,gc
 * </pre>
 *
 * and will produce results like:
 *
 * <pre>
*    Member  | Status | Message
 *   -------- | ------ | --------------------------------------
 *   server-1 | OK     | [true, rvv_exceptions=0, tombstones=1]
 *   server-2 | OK     | [true, rvv_exceptions=0, tombstones=1]
 * </pre>
 *
 * If the function is run <b>without</b> the "gc" option it will only report the number of RVV
 * exceptions found as well as the number of tombstones waiting to be GC'd but will <b>not</b> make
 * any changes.
 * <p>
 * In order to use this function, compile into a .jar and deploy to all servers using gfsh:
 *
 * <pre>
 *   deploy --jar=/path/to/force-tombstone-gc.jar
 * </pre>
 */
public class ForceTombstoneGCFunction implements Function<String[]> {
  private static final long serialVersionUID = 3973943930233187986L;

  private static final Logger logger = LogService.getLogger();

  public static final String ID = "tombstone-gc";

  @Override
  public void execute(FunctionContext<String[]> context) {
    Object[] args = context.getArguments();
    if (args == null || args.length < 1 || args.length > 2) {
      context.getResultSender()
          .sendException(new IllegalArgumentException("Please provide the correct arguments"));
      return;
    }

    String regionName = ((String[]) context.getArguments())[0];
    logger.info("TOMBSTONE_GC: Starting on member {} for region {}",
        context.getMemberName(), regionName);

    Region<?, ?> region = context.getCache().getRegion(regionName);
    if (region == null) {
      context.getResultSender()
          .sendException(new IllegalArgumentException("Region not found: " + regionName));
      return;
    }

    if (region.getAttributes().getDataPolicy().withPartitioning()) {
      PartitionedRegion pr = (PartitionedRegion) region;
      if (pr.getDataStore() == null) {
        logger.info("TOMBSTONE_GC: No local data on this member (accessor) for region {}",
            regionName);
        context.getResultSender().lastResult("No local data on this member (accessor)");
        return;
      }
    }

    Map<RegionVersionVector, LocalRegion> rvvs = getRegionVersionVectors(region);

    TombstoneService tsService = ((InternalCache) context.getCache()).getTombstoneService();
    long tombstonesBefore = tsService.getScheduledTombstoneCount();
    int rvvExceptionsBefore = getExceptionCount(rvvs);

    logger.info("TOMBSTONE_GC: RVV exceptions before GC - {}", rvvExceptionsBefore);
    logger.info("TOMBSTONE_GC: Scheduled tombstones before GC - {}", tombstonesBefore);

    if ((args.length > 1 && "gc".equals(args[1]))) {
      for (Map.Entry<RegionVersionVector, LocalRegion> entry : rvvs.entrySet()) {
        // Build a map of member -> current version (same as what tombstone GC uses)
        Map<VersionSource<?>, Long> gcVersions = new HashMap<>();
        Map<VersionSource, RegionVersionHolder> memberToVersion =
            entry.getKey().getMemberToVersion();
        for (Map.Entry<VersionSource, RegionVersionHolder> sourceHolder : memberToVersion
            .entrySet()) {
          gcVersions.put(sourceHolder.getKey(), sourceHolder.getValue().getVersion());
        }
        tsService.gcTombstones(entry.getValue(), gcVersions, false);
      }

      // Now roll the oplogs for this region
      DiskStore diskStore =
          context.getCache().findDiskStore(region.getAttributes().getDiskStoreName());
      if (diskStore != null) {
        diskStore.forceRoll();
        logger.info("TOMBSTONE_GC: oplog rolled for diskstore {}", diskStore.getName());
      } else {
        logger.info("TOMBSTONE_GC: No diskstore found for region {}", region);
      }

      int rvvExceptionsAfter = getExceptionCount(rvvs);
      long tombstonesAfter = tsService.getScheduledTombstoneCount();

      logger.info("TOMBSTONE_GC: RVV exceptions after GC - {}", rvvExceptionsAfter);
      logger.info("TOMBSTONE_GC: Scheduled tombstones after GC - {}", tombstonesAfter);

      context.getResultSender().sendResult(true);
      context.getResultSender()
          .sendResult("rvv_exceptions=" + (rvvExceptionsBefore - rvvExceptionsAfter));
      context.getResultSender().lastResult("tombstones=" + (tombstonesBefore - tombstonesAfter));
    } else {
      context.getResultSender().sendResult(false);
      context.getResultSender()
          .sendResult("rvv_exceptions=" + rvvExceptionsBefore);
      context.getResultSender().lastResult("tombstones=" + tombstonesBefore);
    }
  }

  private Map<RegionVersionVector, LocalRegion> getRegionVersionVectors(Region<?, ?> region) {
    Map<RegionVersionVector, LocalRegion> result = new HashMap<>();
    if (region.getAttributes().getDataPolicy().withPartitioning()) {
      PartitionedRegion pr = (PartitionedRegion) region;
      for (BucketRegion r : pr.getDataStore().getAllLocalBucketRegions()) {
        result.put(r.getVersionVector(), r);
      }
    } else {
      result.put(((LocalRegion) region).getVersionVector(), (LocalRegion) region);
    }

    return result;
  }

  private int getExceptionCount(Map<RegionVersionVector, LocalRegion> rvvs) {
    int exceptions = 0;

    for (RegionVersionVector rvv : rvvs.keySet()) {
      Map<Object, RegionVersionHolder<Object>> holders = rvv.getMemberToVersion();

      for (Object member : holders.keySet()) {
        exceptions += rvv.getExceptionCount((VersionSource<?>) member);
      }
    }
    return exceptions;
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public boolean isHA() {
    return false;
  }

  @Override
  public boolean optimizeForWrite() {
    return false;
  }

}
