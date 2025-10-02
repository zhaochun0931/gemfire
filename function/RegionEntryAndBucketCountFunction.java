import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.internal.cache.PartitionedRegion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RegionEntryAndBucketCountFunction implements Function<List<String>> {

    @Override
    public void execute(FunctionContext context) {
        Cache cache = (Cache) context.getCache();
        ResultSender<List<String>> sender = context.getResultSender();

        List<String> regionStats = new ArrayList<>();

        // Get all root regions
        Collection<Region<?, ?>> rootRegions = cache.rootRegions();

        for (Region<?, ?> region : rootRegions) {
            addRegionStats(region, regionStats);
        }

        sender.lastResult(regionStats);
    }

    private void addRegionStats(Region<?, ?> region, List<String> regionStats) {
        StringBuilder sb = new StringBuilder();

        sb.append(region.getFullPath())
          .append(" => entries=")
          .append(region.size());

        // If it's a partitioned region, add primary bucket count
        if (region instanceof PartitionedRegion) {
            PartitionedRegion pr = (PartitionedRegion) region;
            int primaryBuckets = pr.getDataStore() != null
                    ? pr.getDataStore().getLocalPrimaryBucketsListTestOnly().size()
                    : 0;
            sb.append(", primaryBuckets=").append(primaryBuckets);
        }

        regionStats.add(sb.toString());

        // Also include subregions
        for (Region<?, ?> subRegion : region.subregions(true)) {
            addRegionStats(subRegion, regionStats);
        }
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public boolean isHA() {
        return false;
    }
}
