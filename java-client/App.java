import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.*;

public class App {
    public static void main(String[] args) {
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .set("log-level", "WARN")
                .create();

        // Ensure the region exists on client
        Region<Integer, String> region = cache.<Integer, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("exampleRegion");

        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            region.put(i, "value-" + i);
        }

        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start) + " ms");
        System.out.println("Completion timestamp: " + new java.util.Date(end));

        cache.close();
    }
}
