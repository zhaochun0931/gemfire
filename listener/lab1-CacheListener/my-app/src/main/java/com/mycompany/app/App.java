package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class App {
    public static void main(String[] args) throws Exception {

        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("172.16.204.139", 10334)
                .setPoolSubscriptionEnabled(true) // Required for listeners
                .create();

        // Create region and attach the listener
        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
                .addCacheListener(new MySimpleListener()) 
                .create("exampleRegion");

        region.registerInterestForAllKeys();

        // 1. CREATE: Put 1000 entries
        for (int i = 1; i <= 1000; i++) {
            String key = "key" + i;
            String value = "value" + i;
            region.put(key, value);
        }
        System.out.println("✅ Inserted 1000 entries into the region!");

        // 2. UPDATE: Overwrite an existing key
        System.out.println("🔄 Updating 'key1' to trigger the update event...");
        region.put("key1", "SUPER_NEW_UPDATED_VALUE");

        // 3. DESTROY: Delete a specific key
        System.out.println("🗑️ Deleting 'key500' to trigger the destroy event...");
        region.destroy("key500");

        // Wait a brief moment to ensure the background listener thread prints to the console
        Thread.sleep(2000);

        cache.close();
    }
}
