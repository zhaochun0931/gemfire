package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class App {
    public static void main(String[] args) throws Exception {

        // 1. Connect to the cluster (No subscriptions needed for Async!)
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("172.16.204.139", 10334)
                .create();

        // 2. Connect to the region (No listener attached here!)
        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("exampleRegion");

        // 3. CREATE: Put 1000 entries
        System.out.println("🚀 Sending 1000 entries to the server...");
        for (int i = 1; i <= 1000; i++) {
            String key = "key" + i;
            String value = "value" + i;
            region.put(key, value);
        }

        // 4. UPDATE
        System.out.println("🔄 Updating 'key1'...");
        region.put("key1", "SUPER_NEW_UPDATED_VALUE");

        // 5. DESTROY
        System.out.println("🗑️ Deleting 'key500'...");
        region.destroy("key500");

        System.out.println("✅ Client operations complete! The server will process the async events in the background.");

        cache.close();
    }
}
