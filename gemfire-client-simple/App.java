import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );

        ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334).create();
        Region<String, String>
                helloWorldRegion =
                cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("helloWorld");

        for (int i=0; i<10000; i++){
            helloWorldRegion.put(String.valueOf(i), "HelloWorldValue---"+ String.valueOf(i));
            String value1 = helloWorldRegion.get(String.valueOf(i));
            System.out.println(value1);
        }

        cache.close();
    }
}
