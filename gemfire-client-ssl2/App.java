import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class App {

    static Properties gemfireProperties( ) {

        Properties gemfireProperties = new Properties();
        gemfireProperties.put("log-level", "all");
        gemfireProperties.put("log-file", "/tmp/GFSSLClient.log");

        String keyStoreFilePath1="/tmp/tls.keystore";
        String keyStorePassword1="password";
        String trustStoreFilePath1="/tmp/tls.truststore";
        String trustStorePassword1="password";

        gemfireProperties.setProperty("log-level", "config");
        gemfireProperties.setProperty("ssl-enabled-components", "all");
        gemfireProperties.setProperty("ssl-keystore", keyStoreFilePath1);
        gemfireProperties.setProperty("ssl-keystore-password", keyStorePassword1);
        gemfireProperties.setProperty("ssl-truststore", trustStoreFilePath1);
        gemfireProperties.setProperty("ssl-truststore-password", trustStorePassword1);

        return gemfireProperties;
    }

    public static void main(String[] args) {

        System.out.println("Sample GemFire client");

        Properties props = new Properties();
        /*props.put("log-level", "all");
        props.put("log-file", "/tmp/GFSSLClient.log");*/
        //props.put("xxxx", "yyyy");

        ClientCache cache = new ClientCacheFactory(gemfireProperties()).addPoolLocator("localhost", 10334).create();


        // create a local region that matches the server region
        Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
                .create("exampleRegion");

        region.put("k1", "v1");
        region.put("k2", "v2");
        region.put("k3", "v3");
        region.put("k4", "v4");
        region.put("k5", "v5");

        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add("k1");
        searchKeys.add("k3");
        searchKeys.add("k5");
        Map<String, String> resultMap =  region.getAll(searchKeys);

        resultMap.forEach((k,v)->{
            System.out.println("[" + k + ", " + v + "]");
        });
    }
}
