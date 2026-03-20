package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class App {
    public static void main(String[] args) throws Exception {

        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("172.16.204.139", 10334)
                .create();

        // 1. Create the region FIRST (without the CacheWriter)
        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("exampleRegion");

        // 2. Attach the CacheWriter using the AttributesMutator!
        region.getAttributesMutator().setCacheWriter(new MyCacheWriter());

        // 3. Send a VALID value
        System.out.println("\n➡️ Attempting to put a valid value...");
        region.put("key1", "APPROVED_VALUE");
        System.out.println("✅ Success! 'key1' was saved to the server.");

        // 4. Send an INVALID value to trigger the veto
        System.out.println("\n➡️ Attempting to put an invalid value...");
        try {
            region.put("key2", "REJECT");
            // If the writer fails to veto, this line will print
            System.out.println("❌ Uh oh, 'key2' was saved anyway!"); 
        } catch (CacheWriterException e) {
            // The CacheWriter throws the exception back to our app, saving the day!
            System.out.println("🛑 Caught exception from CacheWriter: " + e.getMessage());
        }

        // 5. Trigger a Destroy
        System.out.println("\n➡️ Attempting to delete 'key1'...");
        region.destroy("key1");

        cache.close();
    }
}

