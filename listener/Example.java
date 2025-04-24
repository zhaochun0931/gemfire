import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class Example {

    public static void main(String[] args) {
        // Connect to the locator using default port 10334
        ClientCache cache = new ClientCacheFactory()
            .addPoolLocator("127.0.0.1", 10334)
            .set("log-level", "WARN")
            .create();

        // Create a region and add a cache listener
        ClientRegionFactory<Integer, String> clientRegionFactory =
            cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        clientRegionFactory.addCacheListener(new ExampleCacheListener());
        Region<Integer, String> region = clientRegionFactory.create("example-region");

        // Put 10 entries into the region
        for (int i = 1; i <= 10; i++) {
            region.put(i, "Value " + i);
        }
        
        System.out.println("Created 10 entries.");

        cache.close();
    }
}
