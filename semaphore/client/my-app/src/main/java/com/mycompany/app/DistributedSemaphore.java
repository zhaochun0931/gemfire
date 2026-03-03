package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import java.util.List;




public class DistributedSemaphore {
    private final String name;
    private final Region<String, Integer> region;

    public DistributedSemaphore(ClientCache cache, String name, int initialPermits) {
        this.name = name;

        // Check if the region is already defined on the client
        Region<String, Integer> existingRegion = cache.getRegion("SemaphoreRegion");

        if (existingRegion == null) {
            // Create the client proxy to talk to the server
            this.region = cache.<String, Integer>createClientRegionFactory(ClientRegionShortcut.PROXY)
                               .create("SemaphoreRegion");
        } else {
            this.region = existingRegion;
        }

        // Initialize permits on the server if not already set
        this.region.putIfAbsent(name, initialPermits);
    }

    public boolean tryAcquire() {
        try {
            Execution execution = FunctionService.onRegion(this.region).setArguments(this.name);
            ResultCollector<?, ?> rc = execution.execute("AcquirePermitFunction");
            List<?> results = (List<?>) rc.getResult();
            return (Boolean) results.get(0);
        } catch (Exception e) {
            System.err.println("Error acquiring semaphore: " + e.getMessage());
            return false;
        }
    }

    public void release() {
        // Increment the permit count back on the server
        this.region.compute(this.name, (k, v) -> (v == null) ? 1 : v + 1);
    }
}
