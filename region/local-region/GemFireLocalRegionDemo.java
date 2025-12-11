import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.Region;

public class GemFireLocalRegionDemo {
    public static void main(String[] args) {
        // 1️⃣ Connect to locator
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)  // Change to your locator host/port
                .set("log-level", "info")
                .create();

        System.out.println("Connected to locator.");

        // 2️⃣ Create a LOCAL client region (does NOT send data to server)
        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.LOCAL)
                .create("test");

        System.out.println("Created LOCAL client region: /test");

        // 3️⃣ Put a value
        region.put("k1", "test123");
        region.put("k2", "hello world");

        System.out.println("Values put into LOCAL region.");

        // 4️⃣ Get values locally
        String v1 = region.get("k1");
        String v2 = region.get("k2");

        System.out.println("Key k1 = " + v1);
        System.out.println("Key k2 = " + v2);

        // 5️⃣ Close cache
        cache.close();
        System.out.println("Cache closed.");
    }
}

