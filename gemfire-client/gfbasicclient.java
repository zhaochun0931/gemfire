import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

public class gfbasicclient {
    public static void main(String[] args) {
        ClientCache cache = new ClientCacheFactory()
                .set("cache-xml-file", "/root/client-cache.xml")
                .create();

        Region<String, String> region = cache.getRegion("exampleRegion");

        region.put("key1", "value1");
        region.put("key2", "value2");

        System.out.println("Entries written to GemFire server successfully.");

        cache.close();
    }
}
