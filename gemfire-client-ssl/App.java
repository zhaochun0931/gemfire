import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.InterestResultPolicy;



public class App {

  public static final String EXAMPLE_REGION_NAME = "exampleRegion";

  public static void main(String[] args) throws Exception {

    System.out.println("Connecting to the distributed system and creating the cache.");

    ClientCache cache = new ClientCacheFactory()
        .set("name", "ClientWorkerPut")
        .set("cache-xml-file", "ClientProducer.xml")
        .set("log-level", "config")
        .set("log-file", "producer.log")
	.set("ssl-enabled-components", "all")
	.set("ssl-keystore","/tmp/tls.keystore")
	.set("ssl-keystore-password", "password")
	.set("ssl-truststore", "/tmp/gemfire.truststore")
	.set("ssl-truststore-password", "password")
	.addPoolLocator("10.0.0.4", 10334)
        .create();


    // Get the exampleRegion
    Region<String, String> exampleRegion = cache.getRegion(EXAMPLE_REGION_NAME);

    for (int count = 0; count < 1000; count++) {
      String key = "4key" + count;
      exampleRegion.put(key,key);
    }

    // Close the cache and disconnect from GemFire distributed system
    System.out.println("Closing the cache and disconnecting.");
    cache.close();
  }
}
