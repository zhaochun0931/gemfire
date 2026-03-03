package com.mycompany.app;


import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {




	    ClientCache cache = new ClientCacheFactory()
    .addPoolLocator("localhost", 10334) // Ensure this matches your GFSH locator port
    .setPoolSubscriptionEnabled(true)
    .create();



    DistributedSemaphore semaphore = new DistributedSemaphore(cache, "my-api-limit", 5);

    if (semaphore.tryAcquire()) {
        try {
            System.out.println("Permit acquired! Performing task...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            System.out.println("Permit released.");
        }
    } else {
        System.out.println("Too many concurrent users. Try again later.");
    }


    }
}
