package com.mycompany.app;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class App {

    public static void main(String[] args) {
        
        // 1. Initialize the ClientCache
        // We explicitly set a higher read timeout to give the server time to process
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334) // Replace with actual Locator Host/Port
                .setPoolReadTimeout(60000) // 60 seconds (Default is often 10s, which is too short for large removes)
                .setPoolRetryAttempts(3)
                .create();

        try {
            // 2. Create the Client Region Proxy
            // PROXY means the client doesn't store data locally; all operations go to the server
            Region<String, Object> region = cache
                    .<String, Object>createClientRegionFactory(ClientRegionShortcut.PROXY)
                    .create("ExampleRegion");

            // 3. Fetch or generate the list of keys you want to remove
            List<String> keysToRemove = getKeysToRemove(region);

            System.out.println("Total keys to remove: " + keysToRemove.size());

            // 4. Execute the Batched RemoveAll
            // Batching prevents the "Pool unexpected socket timed out" error
            int batchSize = 1000; 
            performBatchedRemoveAll(region, keysToRemove, batchSize);

        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to clear cache region: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 5. Clean up resources
            if (cache != null && !cache.isClosed()) {
                cache.close();
            }
        }
    }

    /**
     * Safely removes keys in chunks to prevent network saturation and server-side timeouts.
     */
    private static void performBatchedRemoveAll(Region<String, Object> region, List<String> keys, int batchSize) {
        if (keys == null || keys.isEmpty()) {
            System.out.println("No keys provided for removal.");
            return;
        }

        for (int i = 0; i < keys.size(); i += batchSize) {
            int end = Math.min(keys.size(), i + batchSize);
            List<String> batch = keys.subList(i, end);
            
            try {
                // The actual Geode API call
                region.removeAll(batch);
                System.out.println("Successfully removed batch: " + i + " to " + end);
            } catch (Exception e) {
                System.err.println("FAILED to remove batch " + i + " to " + end + ". Reason: " + e.getMessage());
                // Depending on your requirements, you might want to re-throw or retry here
            }
        }
    }

    /**
     * Helper method to simulate getting the keys. 
     * In a real scenario, you might get this from a query or keySetOnServer().
     */
    private static List<String> getKeysToRemove(Region<String, Object> region) {
        // Example: If you need to clear EVERYTHING, you can fetch all keys from the server.
        // NOTE: keySetOnServer() can also be expensive on huge regions. 
        return new ArrayList<>(region.keySetOnServer());
    }
}
