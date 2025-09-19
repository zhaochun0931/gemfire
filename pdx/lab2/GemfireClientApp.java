import org.apache.geode.cache.client.*;
import org.apache.geode.cache.*;

public class GemfireClientApp {
    public static void main(String[] args) {
        // Create a client cache and connect to locator
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("172.16.204.135", 10334)  // locator host + port
                .set("log-level", "config")
                .create();

        // Create (or get) the client region that maps to server region "LTVData"
        ClientRegionFactory<String, LongTermVelocityGridValue> regionFactory =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);

        Region<String, LongTermVelocityGridValue> region =
                regionFactory.create("LTVData");

        // Put a value
        region.put("42134F72A4BEA...",
                   new LongTermVelocityGridValue("20250831,4.937...", 1756616400000L));

        // Get the value back
        LongTermVelocityGridValue value = region.get("42134F72A4BEA...");
        System.out.println("Retrieved: " + value);

        cache.close();
    }
}
