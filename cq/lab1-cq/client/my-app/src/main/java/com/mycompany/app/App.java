package com.mycompany.app;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqListener;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.CqResults;
import org.apache.geode.cache.query.QueryService;

public class App {

    public static void main(String[] args) {
        
        System.out.println("Starting CQ Client...");
        ClientCache cache = new ClientCacheFactory()
                // Use addPoolServer("127.0.0.1", 40404) if locator routing gives you trouble again
                .addPoolLocator("172.16.204.139", 10334) 
                .setPoolSubscriptionEnabled(true) // CRITICAL
                .setPdxReadSerialized(true) // <--- THE MAGIC FIX
                .set("log-level", "WARN")
                .create();

        try {
            QueryService queryService = cache.getQueryService();
            String oqlQuery = "SELECT * FROM /Trades t WHERE t.price > 100.0";

            CqAttributesFactory cqAttributesFactory = new CqAttributesFactory();
            cqAttributesFactory.addCqListener(new CqListener() {
                @Override
                public void onEvent(CqEvent event) {
                    System.out.println("\n  [CQ ALERT!] ---> Action: " + event.getBaseOperation());
                    System.out.println("  [CQ ALERT!] ---> Match:  " + event.getNewValue() + "\n");
                }
                @Override public void onError(CqEvent event) { System.err.println("CQ Error!"); }
                @Override public void close() {}
            });

            CqAttributes cqAttributes = cqAttributesFactory.create();
            CqQuery myCq = queryService.newCq("HighValueTradesCQ", oqlQuery, cqAttributes);
            
            // Forces an immediate check to ensure the server isn't throwing serialization errors
            CqResults<?> initialResults = myCq.executeWithInitialResults();
            System.out.println("CQ Registered Successfully. Existing matches: " + initialResults.size());
            System.out.println("Listening for new trades...\n");

            // Keep the client running indefinitely
            Thread.sleep(Long.MAX_VALUE); 

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cache.close();
        }
    }
}
