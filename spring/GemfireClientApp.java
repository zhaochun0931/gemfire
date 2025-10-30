import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GemfireClientApp {

    public static void main(String[] args) {
        SpringApplication.run(GemfireClientApp.class, args);
    }

    @Bean
    public ClientCache gemfireClientCache() {
        return new ClientCacheFactory()
                .addPoolLocator("localhost", 10334) // adjust for your server
                .create();
    }

    @Bean
    public Region<String, String> exampleRegion(ClientCache cache) {
        ClientRegionFactory<String, String> factory =
                cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        return factory.create("ExampleRegion");
    }

    @Bean
    public CommandLineRunner runner(Region<String, String> region) {
        return args -> {
            region.put("hello", "world");
            System.out.println("Value from GemFire region: " + region.get("hello"));
        };
    }
}
