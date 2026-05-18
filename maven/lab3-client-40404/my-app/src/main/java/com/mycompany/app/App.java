
package com.mycompany.app;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;


/**
 * Hello world!
 */


public class App {

    public static void main(String[] args) {
        
        // 1. Connect directly to the Server (Bypassing the Locator)
        System.out.println("Connecting directly to GemFire server on port 40404...");
        ClientCache cache = new ClientCacheFactory()
                .addPoolServer("172.16.204.139", 40404) // <--- The magic line
                .create();

        // 2. Connect to a specific Region (Table/Cache)
        // Note: Change "myRegion" to the actual name of your server-side region
        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("myRegion");

        // 3. Test the connection with a Put and Get
        System.out.println("Putting data into the cache...");
        region.put("greeting", "Hello from the direct Java Client!");

        System.out.println("Retrieving data from the cache...");
        String value = region.get("greeting");
        
        System.out.println("Success! Value retrieved: " + value);

        // 4. Clean up and close
        cache.close();
    }
}
