package com.mycompany.app;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import java.util.Collections;



/**
 * Hello world!
 */
public class App implements Function<Object> {


    @Override
    public void execute(FunctionContext<Object> context) {
        RegionFunctionContext rfc = (RegionFunctionContext) context;
        Region<String, Integer> region = rfc.getDataSet();
        String semaphoreName = (String) context.getArguments();

        // compute() handles the locking of the entry automatically
        Integer updatedValue = region.compute(semaphoreName, (key, currentPermits) -> {
            if (currentPermits == null || currentPermits <= 0) {
                return currentPermits; // No permits to give
            }
            return currentPermits - 1;
        });

        // If the value decreased or stayed positive, we 'acquired' it.
        // Note: This logic depends on how you initialized the region.
        boolean success = (updatedValue != null && updatedValue >= 0);
        context.getResultSender().lastResult(success);
    }

    @Override
    public String getId() {
        return "AcquirePermitFunction";
    }

    @Override
    public boolean optimizeForWrite() {
        return true; // Crucial for performance and consistency
    }



}
