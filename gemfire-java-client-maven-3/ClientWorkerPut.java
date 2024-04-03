import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.InterestResultPolicy;



public class ClientWorkerPut {

  public static final String EXAMPLE_REGION_NAME = "exampleRegion";

  public static void main(String[] args) throws Exception {

    System.out.println("Connecting to the distributed system and creating the cache.");

    ClientCache cache = new ClientCacheFactory()
        .set("name", "ClientWorkerPut")
        .set("cache-xml-file", "/root/client-cache.xml")
        .set("log-level", "config")
        .set("log-file", "producer.log")
        .create();


    // Get the exampleRegion
    Region<String, String> exampleRegion = cache.getRegion(EXAMPLE_REGION_NAME);

    for (int count = 0; count < 1000; count++) {
      String key = "2key" + count;
      exampleRegion.put(key,key);
    }

    // Close the cache and disconnect from GemFire distributed system
    System.out.println("Closing the cache and disconnecting.");
    cache.close();
  }
}
