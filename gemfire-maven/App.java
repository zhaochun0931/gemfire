import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;


public class App
{
    public static void main( String[] args )
    {

        ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334).create();
        Region<String, String>
                xxx =
                cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("example-region");

        for (int i=0; i<1000; i++){
            xxx.put(String.valueOf(i), "HelloWorldValue");
            String value1 = xxx.get("1");
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
