package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        // 1. Connect to the GemFire Cluster (Locators)
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .set("log-level", "info")
                .create();

        // 2. Define the region (Should match the server-side region name)
        Region<Integer, String> region = cache.<Integer, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("ExampleRegion");

        int totalEntries = 1_000_000;
        int batchSize = 5_000; // Optimal batch size for most GemFire setups
        Map<Integer, String> buffer = new HashMap<>(batchSize);

        System.out.println("Starting bulk insert of " + totalEntries + " entries...");
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= totalEntries; i++) {
            buffer.put(i, "Value-" + i);

            // 3. When buffer is full, send the batch
            if (i % batchSize == 0) {
                region.putAll(buffer);
                buffer.clear();
                System.out.println("Inserted " + i + " entries...");
            }
        }

        // Catch any remaining entries
        if (!buffer.isEmpty()) {
            region.putAll(buffer);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Success! Total time: " + (endTime - startTime) + "ms");

        cache.close();
    }
}
