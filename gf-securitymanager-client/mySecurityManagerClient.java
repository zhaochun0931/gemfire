import java.util.Properties;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;


public class mySecurityManagerClient {
    public static void main (String[] args)
    {
        Properties properties = new Properties();
        properties.setProperty("security-client-auth-init", UserPasswordAuthInit.class.getName());
        ClientCache cache = new ClientCacheFactory(properties).addPoolLocator("localhost", 10334).create();
        Region<String, String>
                helloWorldRegion =
                cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("helloWorld");
        helloWorldRegion.put("1", "HelloWorldValue");
        String value1 = helloWorldRegion.get("1");
        System.out.println(value1);
        cache.close();
    }
}
