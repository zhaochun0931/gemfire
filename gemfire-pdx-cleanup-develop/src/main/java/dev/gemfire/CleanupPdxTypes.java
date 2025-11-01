/*
 * Copyright 2025 Broadcom. All rights reserved.
 */

package dev.gemfire;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import org.apache.geode.cache.EntryDestroyedException;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.logging.internal.log4j.api.LogService;

/**
 * This function is intended to find region entries that do NOT have a
 * corresponding PDX type defined. This situation may manifest as an
 * IllegalStateException when performing a query. For example:
 * <pre>
 *   java.lang.IllegalStateException: Unknown pdx type=42728320
 *         at gemfire//org.apache.geode.internal.InternalDataSerializer.readPdxSerializable(InternalDataSerializer.java:3125)
 *         at gemfire//org.apache.geode.internal.InternalDataSerializer.basicReadObject(InternalDataSerializer.java:2874)
 *         at gemfire//org.apache.geode.DataSerializer.readObject(DataSerializer.java:3189)
 *         at gemfire//org.apache.geode.internal.util.BlobHelper.deserializeBlob(BlobHelper.java:110)
 *         at gemfire//org.apache.geode.internal.cache.EntryEventImpl.deserialize(EntryEventImpl.java:2067)
 *         ...
 * </pre>
 * <p>
 * Once compiled into a jar, the function can be deployed using {@code gfsh deploy --jar}
 * <p>
 * The function should be executed using gfsh as follows:
 * <pre>
 *   gfsh execute function --id=pdx-cleanup-function --arguments=SIMULATE,REGION
 * </pre>
 * The required arguments are:
 * <ul>
 *   <li>
 *     SIMULATE ("true" or "false") - When "true", the function will only log
 *     those entries it finds that do not have an associated PDX type. When set
 *     to "false", these entries will be removed.
 *   </li>
 *   <li>
 *      REGION - The name of the region to process.
 *   </li>
 * </ul>
 * Note: When executing the function against a REPLICATE region, it is
 * recommended to ALSO use the {@code --member} option to only target a single
 * member.
 * <p>
 * The returned value is the number of entries that have been identified as
 * missing an associated PDX type. These entries will also be logged with messages like:
 * <pre>
 *   PDX_DEBUG: Removed entry=124 with missing pdxType=15339912
 * </pre>
 */
public class CleanupPdxTypes implements Function<Object[]> {

  private static final Pattern UNKNOWN_PDX_RE = Pattern.compile("Unknown pdx type=(\\d+)");
  public static final String ID = "pdx-cleanup-function";
  public static final Logger logger = LogService.getLogger();

  @Override
  public void execute(FunctionContext<Object[]> context) {
    Object[] arguments = context.getArguments();

    if (arguments == null || arguments.length != 2) {
      context.getResultSender().sendException(
          new IllegalArgumentException(ID + " requires 2 arguments: SIMULATE,REGION"));
      return;
    }

    boolean simulate = Boolean.parseBoolean((String) arguments[0]);
    String regionName = arguments[1].toString();

    logger.info("PDX_DEBUG: Executing {} on region {}", ID, regionName);

    Region<?, ?> region = context.getCache().getRegion(regionName);
    if (region == null) {
      logger.warn("PDX_DEBUG: Region {} not found", regionName);
      context.getResultSender().lastResult(0);
      return;
    }

    if (region.getAttributes().getDataPolicy().withPartitioning()) {
      region = PartitionRegionHelper.getLocalPrimaryData(region);
    }

    int entriesRemoved = 0;
    // Iterate region values as PdxInstances and remove entries without a corresponding PdxType.
    for (Map.Entry<?, ?> entry : region.entrySet()) {
      try {
        entry.getValue();
      } catch (IllegalStateException e) {
        if (e.getMessage().contains("Unknown pdx type")) {
          String pdxId;
          Matcher matcher = UNKNOWN_PDX_RE.matcher(e.getMessage());
          if (matcher.find()) {
            pdxId = matcher.group(1);
          } else {
            pdxId = "[unknown - " + e.getMessage() + "]";
          }

          String key = entry.getKey().toString();
          if (simulate) {
            logger.info("PDX_DEBUG: Found entry={} with missing pdxType={}", key, pdxId);
          } else {
            try {
              region.invalidate(entry.getKey());
              region.destroy(entry.getKey());
              logger.info("PDX_DEBUG: Removed entry={} with missing pdxType={}", key,
                  pdxId);
            } catch (EntryDestroyedException | EntryNotFoundException ignored) {
              // Something already removed this entry
            }
          }
          entriesRemoved++;
        }
      }
    }

    context.getResultSender().lastResult(entriesRemoved);
  }

  @Override
  public String getId() {
    return ID;
  }

}
