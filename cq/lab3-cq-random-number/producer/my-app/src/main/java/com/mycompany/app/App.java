package com.mycompany.app;

/**
 * Hello world!
 */


import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.concurrent.ThreadLocalRandom;

public class App {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Producer...");

        ClientCache cache = new ClientCacheFactory()
                //.addPoolLocator("127.0.0.1", 10334)
                .addPoolLocator("172.16.204.139", 10334)
                .set("log-level", "WARN")
                .create();

        try {
            // Create the proxy region (Key=Integer, Value=Integer)
            Region<Integer, Integer> region = cache.<Integer, Integer>createClientRegionFactory(ClientRegionShortcut.PROXY)
                    .create("NumbersRegion");

            System.out.println("Generating 20 random numbers (0-100)...");

            for (int i = 1; i <= 20; i++) {
                // Generate a random value between 0 and 100
                int randomValue = ThreadLocalRandom.current().nextInt(0, 101);
                
                // Use 'i' as the key, and the random number as the value
                region.put(i, randomValue);
                
                System.out.println("Pushed -> Key: " + i + " | Value: " + randomValue);
                
                // Pause for 500 milliseconds
                Thread.sleep(500);
            }

            System.out.println("Finished generating data.");

        } finally {
            cache.close();
        }
    }
}
