import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.*;
import java.util.Date;

public class App {

    public static void main(String[] args) throws Exception {

        ClientCache cache = new ClientCacheFactory().addPoolLocator("localhost", 10334).create();
        Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY).create("exampleRegion");

        System.out.println("Putting 10000 entries");
        System.out.println("Start: " + new Date());

        for (int i = 1; i < 10001; i++){
            region.put("" + i, " " + i + " hello world");

        }

        System.out.println("Finish:" + new Date());

	      cache.close();

    }
}
