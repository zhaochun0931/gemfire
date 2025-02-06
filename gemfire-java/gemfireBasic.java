import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;


public class gemfireBasic
{
    public static void main( String[] args )
    {

        ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334).create();
        Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("exampleRegion");

        for (int i=0; i<1000; i++){
            region.put(String.valueOf(i), "HelloWorldValue");
            String value1 = region.get("1");
            System.out.println(value1);
        }

        cache.close();

        if (cache.isClosed()) {
            System.out.println("GemFire cache is closed.");
        } else {
            System.out.println("GemFire cache is open.");
        }

    }
}
