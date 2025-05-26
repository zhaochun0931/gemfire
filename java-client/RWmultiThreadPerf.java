import org.apache.geode.cache.client.*;
import org.apache.geode.cache.*;

import java.util.concurrent.*;
import java.util.*;

public class RWmultiThreadPerf {
    public static void main(String[] args) {
        // Connect to the GemFire cluster
        ClientCacheFactory cacheFactory = new ClientCacheFactory();
        cacheFactory.addPoolLocator("localhost", 10334); // Replace with your locator
        ClientCache cache = cacheFactory.create();

        Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                                             .create("example-region"); // Replace with your region name

        // Write Performance Test
        int writeCount = 100000; // Number of writes
        // measureWritePerformance(region, writeCount);
        measureWritePerformanceMultithreaded(region, writeCount, 10);

        // Read Performance Test
        int readCount = 100000; // Number of reads
        measureReadPerformance(region, readCount);

        cache.close();
    }

    private static void measureWritePerformanceMultithreaded(Region<String, String> region, int count, int threads) {
    System.out.println("Starting Multithreaded Write Performance Test...");
    ExecutorService executor = Executors.newFixedThreadPool(threads);

    long start = System.currentTimeMillis();
    for (int t = 0; t < threads; t++) {
        executor.execute(() -> {
            for (int i = 0; i < count / threads; i++) {
                region.put(Thread.currentThread().getId() + "-key" + i, "value" + i);
            }
        });
    }

    executor.shutdown();
    try {
        executor.awaitTermination(10, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    long end = System.currentTimeMillis();
    double throughput = (count * 1000.0) / (end - start);
    System.out.println("Write Throughput (Multithreaded): " + throughput + " ops/sec");
    }



    // private static void measureWritePerformance(Region<String, String> region, int count) {
    //     System.out.println("Starting Write Performance Test...");
    //     long start = System.currentTimeMillis();

    //     for (int i = 0; i < count; i++) {
    //         region.put("key" + i, "value" + i);
    //     }

    //     long end = System.currentTimeMillis();
    //     double throughput = (count * 1000.0) / (end - start);
    //     System.out.println("Write Throughput: " + throughput + " ops/sec");
    // }

    private static void measureReadPerformance(Region<String, String> region, int count) {
        System.out.println("Starting Read Performance Test...");
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            region.get("key" + i);
        }

        long end = System.currentTimeMillis();
        double throughput = (count * 1000.0) / (end - start);
        System.out.println("Read Throughput: " + throughput + " ops/sec");
    }
}
