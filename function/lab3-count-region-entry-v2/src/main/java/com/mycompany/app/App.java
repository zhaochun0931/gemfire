package com.mycompany.app;

import java.time.Instant;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;

/**
 * GemFire Function (v2) to retrieve the size of a specific region.
 * Implements Function<String> where String is the expected ID type.
 */
public class App implements Function<String> {

    /**
     * The core logic of the function executed on the server.
     * @param context Provides access to arguments and the result sender.
     */
    @Override
    public void execute(FunctionContext context) {
        // --- NEW LOGIC: Capture the timestamp ---
        Instant executionTimestamp = Instant.now();
        System.out.println("[Appv2] Function executed at: " + executionTimestamp);

        // Retrieve arguments passed via gfsh (--arguments) or Java client (.setArguments)
        Object rawArgs = context.getArguments();
        String regionName;

        // Logic to extract the region name string from different possible argument formats
        if (rawArgs instanceof String) {
            regionName = (String) rawArgs;
        } else if (rawArgs instanceof String[]) {
            // Handles cases where arguments are passed as a list/array
            regionName = ((String[]) rawArgs)[0];
        } else if (rawArgs == null) {
            // Safety check: prevent NullPointerException if no arguments are provided
            throw new FunctionException("No region name provided in arguments.");
        } else {
            // Throw exception if the argument is an unexpected data type
            throw new FunctionException("Unsupported argument type: " + rawArgs.getClass());
        }

        // Access the cache instance running on this server and look up the region by name
        Region<Object, Object> region = CacheFactory.getAnyInstance().getRegion(regionName);

        // If region exists, get entry count; otherwise return -1 to signal it's missing
        int count = (region == null ? -1 : region.size());

        // --- NEW LOGIC: Format the result to include the timestamp ---
        String resultPayload = String.format("Size: %d | Executed At: %s", count, executionTimestamp.toString());

        // Send the final result back to the caller (gfsh or client app)
        context.getResultSender().lastResult(resultPayload);
    }

    /**
     * Unique identifier for the function.
     * Used in GFSH: 'execute function --id=Appv2'
     */
    @Override
    public String getId() {
        return "Appv2";
    }

    /**
     * Informs GemFire that this function sends results back to the caller.
     * Must be true if using context.getResultSender().
     */
    @Override
    public boolean hasResult() {
        return true;
    }

    /**
     * High Availability: If true, the function will be re-executed
     * on another node if the primary node fails during execution.
     */
    @Override
    public boolean isHA() {
        return true;
    }
}