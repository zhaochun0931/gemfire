package com.mycompany.app;


import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

public class MySimpleListener extends CacheListenerAdapter<String, String> {

    @Override
    public void afterCreate(EntryEvent<String, String> event) {
        System.out.println("👀 [LISTENER ALERT] New data created! " +
                "Key: '" + event.getKey() + 
                "', Value: '" + event.getNewValue() + "'");
    }

    @Override
    public void afterUpdate(EntryEvent<String, String> event) {
        System.out.println("👀 [LISTENER ALERT] Data was updated! " +
                "Key: '" + event.getKey() + 
                "', Old Value: '" + event.getOldValue() + 
                "', New Value: '" + event.getNewValue() + "'");
    }

    @Override
    public void afterDestroy(EntryEvent<String, String> event) {
        System.out.println("👀 [LISTENER ALERT] Data was deleted! Key: '" + event.getKey() + "'");
    }
}
