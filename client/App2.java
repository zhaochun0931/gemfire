package com.mycompany.app;

import org.apache.geode.cache.client.*;
import org.apache.geode.cache.*;

public class App2 {
    public static void main(String[] args) {

        // ✅ Create a client cache and connect to locator (specify host + port)
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("172.16.204.135", 10334)  // change host + port
                .set("log-level", "INFO")
                .create();

        // ✅ Create client region (proxy or caching-proxy)
        ClientRegionFactory<String, String> regionFactory =
                cache.<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);

        Region<String, String> region = regionFactory.create("demoRegion");

        // Transaction Manager
        CacheTransactionManager txManager = cache.getCacheTransactionManager();

        try {
            txManager.begin();

            region.put("key1", "value1");
            region.put("key2", "value2");

            txManager.commit();
            System.out.println("Transaction committed successfully.");

            // ✅ Verify values are in region
            System.out.println("region.get(key1) = " + region.get("key1"));
            System.out.println("region.get(key2) = " + region.get("key2"));

        } catch (Exception e) {
            System.err.println("Transaction failed, rolling back: " + e.getMessage());
            txManager.rollback();
        }

        cache.close();
    }
}
