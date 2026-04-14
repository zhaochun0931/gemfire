package com.mycompany.app;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.Region;
import java.util.Map;

/**
 * Hello world!
 */
public class App {

    public static final String EXAMPLE_REGION_NAME = "exampleRegion";

    public static void main(String[] args) throws Exception {

        System.out.println("Hello World!");
        System.out.println("Connecting to the distributed system and creating the cache.");

        ClientCache cache = new ClientCacheFactory()
//                .addPoolLocator("localhost", 10334)
                .addPoolLocator("172.16.204.139", 10334)
                .create();

        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
                .create("exampleRegion");

        System.out.println("Putting initial entry: key1");
        region.put("key1", "Hello1");

        // Set sleep time in a variable to print it cleanly
        int sleepTimeMs = 30000;
        System.out.println("I will sleep for " + (sleepTimeMs / 1000) + " seconds and input another entries...");

        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Woke up! Putting new entries into the region.");
        region.put("key2", "hello2");
        region.put("key3", "hello3");

        System.out.println("\n--- Getting and printing entries directly using region.get() ---");
        System.out.format("key = key1, value = %s\n", region.get("key1"));
        System.out.format("key = key2, value = %s\n", region.get("key2"));
        System.out.format("key = key3, value = %s\n", region.get("key3"));

        System.out.println("\n--- Iterating through the region.entrySet() ---");
        for (Map.Entry<String, String> entry : region.entrySet()) {
            System.out.format("key = %s, value = %s\n", entry.getKey(), entry.getValue());
        }

        cache.close();
        System.out.println("Cache closed successfully.");
    }
}