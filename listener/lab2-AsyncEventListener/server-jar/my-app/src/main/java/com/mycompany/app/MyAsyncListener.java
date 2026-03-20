package com.mycompany.app;

import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class MyAsyncListener implements AsyncEventListener {

    // 1. Create a proper Logger instance
    private static final Logger logger = LogManager.getLogger(MyAsyncListener.class);

    @Override
    public boolean processEvents(List<AsyncEvent> events) {
        // 2. Use logger.info() instead of System.out.println()
        logger.info("📦 [ASYNC BATCH] Processing " + events.size() + " events in the background...");
        
        for (AsyncEvent event : events) {
            String operation = event.getOperation().toString();
            String key = (String) event.getKey();
            
            if (operation.contains("CREATE") || operation.contains("UPDATE")) {
                logger.info("   -> ASYNC SAVE: Key '" + key + "' | Value '" + event.getDeserializedValue() + "'");
            } else if (operation.contains("DESTROY")) {
                logger.info("   -> ASYNC DELETE: Key '" + key + "'");
            }
        }
        
        return true; 
    }

    @Override
    public void close() { }
}
