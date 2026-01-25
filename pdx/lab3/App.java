package org.example;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

public class App {
    public static void main(String[] args) {
        
        // 1. Configure PDX Serializer
        // The pattern "org.example.*" tells GemFire to handle all classes in that package
        ReflectionBasedAutoSerializer serializer = 
            new ReflectionBasedAutoSerializer("org.example.User");

        // 2. Create Cache with PDX settings
        ClientCache cache = new ClientCacheFactory()
            .addPoolLocator("localhost", 10334)
            .setPdxSerializer(serializer)
            .setPdxReadSerialized(false) 
            .create();

        // 3. Define the Region
        Region<String, User> region = cache
            .<String, User>createClientRegionFactory(ClientRegionShortcut.PROXY)
            .create("exampleRegion");

        // 4. Input entries
        System.out.println("Storing PDX entries...");
        region.put("user_1", new User("Alice", 30));
        region.put("user_2", new User("Bob", 25));
        region.put("user_3", new User("Charlie", 35));

        // 5. Verify
        User retrieved = region.get("user_1");
        System.out.println("Retrieved from server: " + retrieved);

        cache.close();
    }
}
