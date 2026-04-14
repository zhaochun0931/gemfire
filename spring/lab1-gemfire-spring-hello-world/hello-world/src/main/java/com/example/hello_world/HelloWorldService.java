package com.example.hello_world;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HelloWorldService {

    // Cache the result of this method in a GemFire region named "Hello"
    @Cacheable("Hello")
    public String getHelloValue(String key) {
        simulateSlowDatabaseQuery();
        return LocalDateTime.now().toString();
    }

    // Artificially delay the thread to simulate a slow database lookup
    private void simulateSlowDatabaseQuery() {
        try {
            long timeToSleep = 3000; // 3 seconds
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}