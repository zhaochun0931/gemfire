package org.example;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.SelectResults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;
import org.springframework.geode.config.annotation.EnableClusterAware;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableCachingDefinedRegions
@EnableClusterAware
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * Defines the client-side proxy for 'exampleRegion'.
     * This bean acts as the bridge to the GemFire Server.
     */
    @Bean(name = "exampleRegion")
    public ClientRegionFactoryBean<String, String> exampleRegion(GemFireCache cache) {
        ClientRegionFactoryBean<String, String> exampleRegion = new ClientRegionFactoryBean<>();
        exampleRegion.setCache(cache);
        exampleRegion.setShortcut(ClientRegionShortcut.PROXY);
        return exampleRegion;
    }

    /**
     * Logic to populate data and query the cluster on startup.
     */
    @Bean
    CommandLineRunner setup(GemfireTemplate exampleRegionTemplate) {
        return args -> {
            // --- 1. BULK INSERT WITH PUTALL ---
            int totalEntries = 1000;
            Map<String, String> batchData = new HashMap<>();

            System.out.println(">>> Preparing 1,000 entries...");
            for (int i = 1; i <= totalEntries; i++) {
                batchData.put("Key-" + i, "Value-" + i);
            }

            long startPut = System.currentTimeMillis();
            exampleRegionTemplate.getRegion().putAll(batchData);
            long endPut = System.currentTimeMillis();

            System.out.println(">>> Successfully pushed 1,000 entries via putAll() in " + (endPut - startPut) + "ms");

            // --- 2. QUERYING WITH OQL ---
            // We search for any value that contains the string '99' (e.g., Value-99, Value-199, Value-999)
            String queryString = "SELECT * FROM /exampleRegion WHERE toString().contains('99')";
            
            System.out.println(">>> Executing OQL Query: " + queryString);
            
            long startQuery = System.currentTimeMillis();
            SelectResults<String> results = exampleRegionTemplate.find(queryString);
            long endQuery = System.currentTimeMillis();

            System.out.println(">>> Found " + results.size() + " matches in " + (endQuery - startQuery) + "ms:");
            results.forEach(match -> System.out.println("    Match Found: " + match));

            System.out.println(">>> Application logic complete.");
        };
    }
}
