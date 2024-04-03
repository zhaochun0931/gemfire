import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolFactory;
import org.apache.geode.cache.client.PoolManager;

public class App {

    public static void main(String[] args) {
        ClientCache cache = new ClientCacheFactory()
                .set("cache-xml-file", "/root/client-cache.xml")
                .create();

        PoolFactory poolFactory = PoolManager.createFactory();
        poolFactory.addServer("localhost", 40404);
        poolFactory.setSubscriptionEnabled(true);
        poolFactory.setRetryAttempts(3);

        Pool pool = poolFactory.create("myPool");

        // Use the pool for cache operations
        Region region = cache.getRegion("exampleRegion");
        region.put("key1", "value1");
        region.put("key2", "value2");
	        System.out.println("Entries written to GemFire server successfully.");


        cache.close();
    }
}
