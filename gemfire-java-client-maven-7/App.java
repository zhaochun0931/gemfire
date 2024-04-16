import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

public class App {
    public static void main(String[] args) {
        ClientCache cache = new ClientCacheFactory()
                .set("cache-xml-file", "/tmp/client-cache.xml")
                .create();

        Region<String, String> region = cache.getRegion("exampleRegion");

        // Put data into the region
        region.put("key1", "value1");

        // Get data from the region
        String value = region.get("key1");
        System.out.println("Retrieved value: " + value);

        cache.close();
    }
}
