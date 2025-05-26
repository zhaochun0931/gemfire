import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultipleClient {
    private static final int NUM_CLIENTS = 30003;  // Number of clients to simulate
    private static final int ENTRIES_PER_CLIENT = 100;  // Number of entries per client

    public static void main(String[] args) throws InterruptedException {

        // Create the GemFire client cache
        ClientCache cache = new ClientCacheFactory().addPoolLocator("localhost", 10334).create();

        // Create the region
        Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("exampleRegion");

        // Executor service to simulate multiple clients
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_CLIENTS);

        // Submit tasks for each client to perform operations on the region
        for (int clientId = 0; clientId < NUM_CLIENTS; clientId++) {
            final int id = clientId;
            executorService.submit(() -> simulateClient(id, region));
        }

        // Shutdown the executor service after all tasks have been submitted
        executorService.shutdown();

        // Wait for all clients to finish
        while (!executorService.isTerminated()) {
            // Optionally print the status of execution
            Thread.sleep(1000);
        }

        // Close the cache after all clients have finished their operations
        cache.close();

        // Print cache status
        if (cache.isClosed()) {
            System.out.println("GemFire cache is closed.");
        } else {
            System.out.println("GemFire cache is open.");
        }
    }

    // Simulate the operations of each client
    private static void simulateClient(int clientId, Region<String, String> region) {
        for (int i = 0; i < ENTRIES_PER_CLIENT; i++) {
            // Construct key-value pair for the region
            String key = "key_" + clientId + "_" + i;
            String value = "HelloWorldValue_" + i;

            // Put the entry in the region
            region.put(key, value);

            // Optionally, retrieve and print a value
            if (i == 0) {  // Just print the first value for simplicity
                String value1 = region.get("key_" + clientId + "_0");
                System.out.println("Client " + clientId + " got value: " + value1);
            }

            // Optional sleep to simulate work being done by the client
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Client " + clientId + " finished inserting " + ENTRIES_PER_CLIENT + " entries.");
    }
}
