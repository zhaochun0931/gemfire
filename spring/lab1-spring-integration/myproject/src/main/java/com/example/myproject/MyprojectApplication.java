package com.example.myproject;

import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan // <--- ADD THIS ANNOTATION
public class MyprojectApplication implements CommandLineRunner {

    @Autowired
    private GemFireGateway gemFireGateway;

    @Autowired
    private Region<String, String> dataRegion;

    public static void main(String[] args) {
        SpringApplication.run(MyprojectApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> Sending data through Spring Integration Channel...");

        // Send three messages through the integration pipeline
        gemFireGateway.writeToGemFire("Value-For-Key1", "key1");
        gemFireGateway.writeToGemFire("Value-For-Key2", "key2");
        gemFireGateway.writeToGemFire("Value-For-Key3", "key3");

        System.out.println(">>> Messages sent. Fetching directly from GemFire region proxy to verify...");

        // Pull directly from the GemFire Region to prove they arrived over the wire
        System.out.println("Value of key1: " + dataRegion.get("key1"));
        System.out.println("Value of key2: " + dataRegion.get("key2"));
        System.out.println("Value of key3: " + dataRegion.get("key3"));
    }
}
