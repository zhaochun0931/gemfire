//this function count the total number entries of a region
        
package org.example;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.execute.FunctionException;

public class App implements Function<String> {

    @Override
    public void execute(FunctionContext context) {
        Object rawArgs = context.getArguments();
        String regionName;
    
        if (rawArgs instanceof String) {
            regionName = (String) rawArgs;
        } else if (rawArgs instanceof String[]) {
            regionName = ((String[]) rawArgs)[0];
        } else {
            throw new FunctionException("Unsupported argument type: " + rawArgs.getClass());
        }
    
        Region<Object, Object> region = CacheFactory.getAnyInstance().getRegion(regionName);
    
        int count = (region == null ? -1 : region.size());
    
        context.getResultSender().lastResult(count);
    }


    @Override
    public String getId() {
        return "App"; // the function ID to use in GFSH
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public boolean isHA() {
        return true; // Highly Available
    }
}

