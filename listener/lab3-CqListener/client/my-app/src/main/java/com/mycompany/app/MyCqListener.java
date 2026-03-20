package com.mycompany.app;

import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqListener;

public class MyCqListener implements CqListener {

    @Override
    public void onEvent(CqEvent cqEvent) {
        // This only fires if the data MATCHES our OQL query!
        System.out.println("🚨 [CQ MATCH!] Event type: " + cqEvent.getBaseOperation() + 
                           " | Key: " + cqEvent.getKey() + 
                           " | Value: " + cqEvent.getNewValue());
    }

    @Override
    public void onError(CqEvent cqEvent) {
        System.err.println("❌ [CQ ERROR] Something went wrong: " + cqEvent.getThrowable().getMessage());
    }

    @Override
    public void close() {
        // Cleanup if needed
    }
}
