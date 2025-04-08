import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class gemfireBasic {
    public static void main(String[] args) {

        // Formatter with milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // Capture start timestamp
        LocalDateTime start = LocalDateTime.now();
        System.out.println("⏱️ Before connecting: " + start.format(formatter));

        // Connect to GemFire server
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("127.0.0.1", 10334)
                .create();

        // Capture end timestamp
        LocalDateTime end = LocalDateTime.now();
        System.out.println("✅ After connecting: " + end.format(formatter));

        // Calculate and print duration
        Duration duration = Duration.between(start, end);
        System.out.println("🕒 Connection took: " + duration.toMillis() + " ms");


        cache.close();

        if (cache.isClosed()) {
            System.out.println("GemFire cache is closed.");
        } else {
            System.out.println("GemFire cache is open.");
        }
    }
}
