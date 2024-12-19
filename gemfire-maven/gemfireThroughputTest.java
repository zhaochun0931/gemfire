import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut; // Updated import for RegionShortcut

import java.util.Random;

public class gemfireThroughputTest {

    private static final String REGION_NAME = "exampleRegion";
    private static final int TOTAL_OPERATIONS = 1000000; // Total write operations
    private static final String SERVER_ADDRESS = "localhost"; // Change to your server's address
    private static final int SERVER_PORT = 10334; // Default GemFire server port

    public static void main(String[] args) {
        ClientCache clientCache = new ClientCacheFactory()
                .addPoolLocator(SERVER_ADDRESS, SERVER_PORT)
                .create();

        Region<String, String> region = clientCache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create(REGION_NAME);

        Random random = new Random();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_OPERATIONS; i++) {
            String key = "key-" + i;
            String value = "value-" + random.nextInt(10000); // Random value for demonstration
            region.put(key, value); // Write to Geode
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double throughput = (TOTAL_OPERATIONS / (duration / 1000.0)); // ops/sec

        System.out.printf("Wrote %d entries in %d ms (throughput: %.2f ops/sec)%n", TOTAL_OPERATIONS, duration, throughput);

        clientCache.close();
    }
}
