package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.QueryService;

public class App {
    public static void main(String[] args) throws Exception {

        // 1. Connect (Subscriptions MUST be true for CQs to work)
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("172.16.204.139", 10334)
                .setPoolSubscriptionEnabled(true) 
                .create();

        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("exampleRegion");

        // 2. Set up the Continuous Query
        QueryService queryService = cache.getQueryService();

        // Write standard OQL. We are saying: "Watch /exampleRegion and alert me if the value equals 'URGENT'"
        String oql = "SELECT * FROM /exampleRegion val WHERE val = 'URGENT'";

        // 3. Attach our custom listener to the query
        CqAttributesFactory cqf = new CqAttributesFactory();
        cqf.addCqListener(new MyCqListener());

        // 4. Register and Execute the CQ on the server!
        CqQuery myCq = queryService.newCq("MyUrgentQuery", oql, cqf.create());
        myCq.execute(); 

        System.out.println("✅ Continuous Query registered! Listening for 'URGENT' messages...");
        Thread.sleep(1000); // Brief pause to ensure the server registers the CQ

        // --- LET'S TEST IT ---
        
        System.out.println("\n➡️ Inserting normal message...");
        region.put("msg1", "Just saying hello"); // This will NOT trigger the CQ

        Thread.sleep(500);

        System.out.println("➡️ Inserting URGENT message...");
        region.put("msg2", "URGENT"); // 🚨 THIS WILL TRIGGER THE CQ (CREATE)!

        Thread.sleep(500);

        System.out.println("➡️ Resolving the URGENT message...");
        region.put("msg2", "RESOLVED"); // 🚨 THIS WILL TRIGGER THE CQ (DESTROY)!

        Thread.sleep(2000);
        cache.close();
    }
}
