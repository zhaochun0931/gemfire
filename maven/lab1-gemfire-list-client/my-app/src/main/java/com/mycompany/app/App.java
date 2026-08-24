package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {

        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("gf-locator1", 10334)
                .create();

        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
                .create("exampleRegion");

        for (int i = 1; i <= 1000; i++) {
            String key = "key" + i;
            String value = "value" + i;
            region.put(key, value);
        }

        System.out.println("✅ Inserted 1000 entries into the region!");

        // --- NEW CODE ADDED HERE ---
        System.out.println("⏳ Waiting for 60 seconds... You can now monitor this client from GemFire (gfsh).");
        Thread.sleep(60000); // Pauses execution for 60,000 milliseconds (60 seconds)
        System.out.println("🛑 Time is up! Closing the cache and exiting.");
        // ---------------------------

        cache.close();
    }
}
