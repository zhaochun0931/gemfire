import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.*;
import java.util.Date;

public class PutMultipleEntries {

    public static void main(String[] args) throws Exception {

        ClientCacheFactory ccf = new ClientCacheFactory();

        ccf.addPoolLocator("localhost", 10334);
        ccf.addPoolLocator("localhost", 10335);
        ccf.addPoolLocator("localhost", 10336);


        ClientCache cache = ccf.create();


        Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY).create("region1");

        System.out.println("Putting 10000 entries");
        System.out.println("Start: " + new Date());

        for (int i = 1; i < 10; i++){
            region.put("" + i, " " + i + " hello world");
	    Thread.sleep(1000);

        }

        System.out.println("Finish:" + new Date());

	      cache.close();

    }
}
