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

        region.put("1", "Hello");
        region.put("2", "Hello");
        region.put("3", "Hello");
        region.put("4", "Hello");
        region.put("5", "Hello");


        cache.close();






    }
}
