package com.mycompany.app;


import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.CacheException;
import org.apache.geode.cache.Region;

import java.util.HashMap;
import java.util.Map;


/**
 * Hello world!
 */
public class App {

    private ClientCache clientCache;

    public void initializeCache(String instanceName) {

        System.setProperty("gemfire.log-use-screen", "false");
        
        try {
            if (instanceName == null || instanceName.trim().isEmpty()) {
                instanceName = "default-client";
            }

            System.out.println("Initializing GemFire Client Cache for instance: " + instanceName);

            this.clientCache = new ClientCacheFactory()
                    .set("cache-xml-file", "gemfire-client.xml")
                    .set("statistic-archive-file", "./wlslog/cachelog/" + instanceName + ".gfs")
                    .set("statistic-sample-rate", "2000")
                    .set("statistic-sampling-enabled", "true")
                    .set("archive-file-size-limit", "100")
                    .set("archive-disk-space-limit", "1000")
                    .set("log-file", "./wlslog/cachelog/" + instanceName + ".log")
                    .set("log-file-size-limit", "100")
                    .set("log-disk-space-limit", "1000")
                    .set("log-level", "info")
                    .create();

            System.out.println("GemFire Client Cache successfully created.");

        } catch (CacheException e) {
            System.err.println("GemFire initialization failed. Check your XML file or file path permissions.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during initialization.");
            e.printStackTrace();
        }
    }

    public ClientCache getClientCache() {
        return this.clientCache;
    }

    public void closeCache() {
        if (this.clientCache != null && !this.clientCache.isClosed()) {
            this.clientCache.close();
            System.out.println("GemFire Client Cache closed safely.");
        }
    }

    /**
     * Efficiently loads 1 million entries into the specified region using batching.
     */
    public void loadOneMillionEntries(String regionName) {
        if (this.clientCache == null) {
            System.err.println("Cache is not initialized. Cannot load data.");
            return;
        }

        System.out.println("Fetching region: " + regionName);
        Region<String, String> region = this.clientCache.getRegion(regionName);

        if (region == null) {
            System.err.println("Error: Region '" + regionName + "' not found! Check your gemfire-client.xml.");
            return;
        }

        int totalEntries = 1_000_000;
        int batchSize = 10_000; // Optimal chunk size for network transmission
        Map<String, String> batchMap = new HashMap<>(batchSize);

        System.out.println("Starting bulk load of " + totalEntries + " entries...");
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= totalEntries; i++) {
            // Generate dummy key and value string
            batchMap.put("Key_" + i, "Value_Data_Payload_" + i);

            // Once the batch is full, send it to the cluster
            if (i % batchSize == 0) {
                region.putAll(batchMap);
                batchMap.clear(); // Empty the map for the next batch
                System.out.println("Inserted " + i + " / " + totalEntries + " entries...");
            }
        }

        // Catch any remaining elements if totalEntries wasn't perfectly divisible by batchSize
        if (!batchMap.isEmpty()) {
            region.putAll(batchMap);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Successfully inserted 1,000,000 entries!");
        System.out.println("Total time taken: " + (duration / 1000.0) + " seconds.");
    }

    public static void main(String[] args) {
        
        App initializer = new App();

        // 1. Initialize the client cache
        initializer.initializeCache("appServerInstance01");

        // 2. Load 1 million records into "exampleRegion"
        // (Make sure this region is defined in your gemfire-client.xml)
        initializer.loadOneMillionEntries("exampleRegion");

        // 3. Clean up on exit
        initializer.closeCache();
    }
}