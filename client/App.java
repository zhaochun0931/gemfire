import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;



//        input some values to the region


public class App
{
    public static void main( String[] args ) throws Exception
    {

//        System.out.println( "Hello World!" );


        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .create();
        Region<String, String> region = cache
                .<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
                .create("exampleRegion");


        for (int i = 1; i <= 1000; i++) {
            String key = "key" + i;
            String value = "value" + i;
            region.put(key, value);
        }

        System.out.println("✅ Inserted 1000 entries into the region!");

        cache.close();


    }
}
