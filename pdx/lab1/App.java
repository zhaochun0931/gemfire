import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

public class App {
    public static void main(String[] args) {
        System.out.println("Connecting to GemFire locator...");

        // Create client cache and connect to locator
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("localhost", 10334)
                .set("log-level", "config")
                .setPdxPersistent(false)
                .setPdxReadSerialized(false)
                .create();

        System.out.println("Connected.");

        // Get the region proxy (region must already exist on the server)
        Region<String, Person> region = cache.<String, Person>createClientRegionFactory("PROXY")
                .create("exampleRegion1");

        // Step 1: Put an entry
        Person person = new Person("1", "Alice", 30);
        region.put(person.getId(), person);
        System.out.println("Inserted entry: " + person);

        // Step 2: Get the entry
        Person fetched = region.get("1");
        System.out.println("Fetched entry: " + fetched);

        // Step 3: Update entry
        fetched.setAge(31);
        fetched.setName("Alice Smith");
        region.put(fetched.getId(), fetched);
        System.out.println("Updated entry: " + fetched);

        // Step 4: Read again to verify update
        Person updated = region.get("1");
        System.out.println("Verified entry: " + updated);

        // Clean up
        cache.close();
        System.out.println("Client cache closed.");
    }
}
