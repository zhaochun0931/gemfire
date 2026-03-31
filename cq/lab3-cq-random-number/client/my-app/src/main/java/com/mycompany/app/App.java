package com.mycompany.app;

/**
 * Hello world!
 */

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqListener;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.QueryService;

public class App {

    public static void main(String[] args) {
        System.out.println("Starting CQ Client...");

        // 1. Connect to Cache (Remember to use addPoolServer on 40404 if the locator drops events!)
        ClientCache cache = new ClientCacheFactory()
                //.addPoolLocator("127.0.0.1", 10334) 
                .addPoolLocator("172.16.204.139", 10334) 
                .setPoolSubscriptionEnabled(true) // CRITICAL for listeners
                .set("log-level", "WARN")
                .create();

        try {
            QueryService queryService = cache.getQueryService();
            
            // Querying the values directly. 'n' represents the Integer value.
            String oqlQuery = "SELECT * FROM /NumbersRegion n WHERE n > 90";

            CqAttributesFactory cqAttributesFactory = new CqAttributesFactory();
            cqAttributesFactory.addCqListener(new CqListener() {


                @Override
                public void onEvent(CqEvent event) {
                    Integer key = (Integer) event.getKey();
                    Integer newValue = (Integer) event.getNewValue();

                    // Check WHAT happened to the query
                    if (event.getQueryOperation().isCreate() || event.getQueryOperation().isUpdate()) {
                        System.out.println("\n [📈 MATCH!] Value is > 90!");
                        System.out.println("    -> Key:   " + key);
                        System.out.println("    -> Value: " + newValue + "\n");
                        
                    } else if (event.getQueryOperation().isDestroy()) {
                        System.out.println("\n [📉 DROPPED] Value fell below 90.");
                        System.out.println("    -> Key:   " + key);
                        System.out.println("    -> Value: " + newValue + "\n");
                    }
                }



                @Override public void onError(CqEvent event) { System.err.println("CQ Error!"); }
                @Override public void close() {}
            });

            CqAttributes cqAttributes = cqAttributesFactory.create();
            CqQuery myCq = queryService.newCq("HighNumberTracker", oqlQuery, cqAttributes);
            
            myCq.executeWithInitialResults();
            System.out.println("CQ Registered! Listening for numbers > 90...\n");

            // Keep the client running
            Thread.sleep(Long.MAX_VALUE); 

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cache.close();
        }
    }
}
