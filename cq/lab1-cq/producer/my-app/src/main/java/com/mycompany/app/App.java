package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class App {

    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("Starting Producer Client...");
        ClientCache cache = new ClientCacheFactory()
                //.addPoolLocator("127.0.0.1", 10334)
                .addPoolLocator("172.16.204.139", 10334)
                .set("log-level", "WARN")
                .create();

        try {
            Region<String, Trade> tradesRegion = cache.<String, Trade>createClientRegionFactory(ClientRegionShortcut.PROXY)
                    .create("Trades");

            System.out.println("Pushing test data...");

            System.out.println(" -> Put AAPL (50.0) - Should ignore");
            tradesRegion.put("T1", new Trade("T1", "AAPL", 50.0));
            Thread.sleep(1500); // Small pause for dramatic effect

            System.out.println(" -> Put GOOGL (150.0) - Should trigger CQ");
            tradesRegion.put("T2", new Trade("T2", "GOOGL", 150.0));
            Thread.sleep(1500);

            System.out.println(" -> Put MSFT (99.0) - Should ignore");
            tradesRegion.put("T3", new Trade("T3", "MSFT", 99.0));
            Thread.sleep(1500);

            System.out.println(" -> Update AAPL (110.0) - Should trigger CQ");
            tradesRegion.put("T1", new Trade("T1", "AAPL", 110.0));
            
            System.out.println("Finished pushing data.");

        } finally {
            cache.close();
        }
    }
}
