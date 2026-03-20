package com.mycompany.app;

import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheWriterAdapter;

public class MyCacheWriter extends CacheWriterAdapter<String, String> {

    @Override
    public void beforeCreate(EntryEvent<String, String> event) throws CacheWriterException {
        System.out.println("🛡️ [CACHE WRITER] Intercepted CREATE for key: '" + event.getKey() + "' with value: '" + event.getNewValue() + "'");
        
        // VETO LOGIC: Block the operation if the value is "REJECT"
        if ("REJECT".equals(event.getNewValue())) {
            System.out.println("❌ [CACHE WRITER] Vetoing CREATE! Value not allowed.");
            throw new CacheWriterException("The value 'REJECT' violates data rules!");
        }
    }

    @Override
    public void beforeUpdate(EntryEvent<String, String> event) throws CacheWriterException {
        System.out.println("🛡️ [CACHE WRITER] Intercepted UPDATE for key: '" + event.getKey() + "' with new value: '" + event.getNewValue() + "'");
        
        // VETO LOGIC: Block the operation if the value is "REJECT"
        if ("REJECT".equals(event.getNewValue())) {
            System.out.println("❌ [CACHE WRITER] Vetoing UPDATE! Value not allowed.");
            throw new CacheWriterException("The value 'REJECT' violates data rules!");
        }
    }
    
    @Override
    public void beforeDestroy(EntryEvent<String, String> event) throws CacheWriterException {
        System.out.println("🛡️ [CACHE WRITER] Intercepted DESTROY for key: '" + event.getKey() + "'");
        // You could also veto deletes here! (e.g., prevent deleting an active user account)
    }
}
