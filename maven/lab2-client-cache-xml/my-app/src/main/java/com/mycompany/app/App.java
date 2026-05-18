package com.mycompany.app;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;


/**
 * Hello world!
 */


public class App
{

    public static void main(String[] args) {

        ClientCache cache = new ClientCacheFactory()
                .set("cache-xml-file", "client-cache.xml")
                .create();

        Region<String, String> region = cache.getRegion("exampleRegion");


	    for (int i = 1; i <= 1000; i++) {
            String key = "key" + i;
            String value = "value" + i;
            region.put(key, value);
        }

        System.out.println("✅ Inserted 1000 entries into the region!");


        cache.close();
    }

}
