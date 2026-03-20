package com.mycompany.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

public class App {
  public static final int ITERATIONS = 100;

  public static void main(String[] args) throws Exception { // <-- Added throws Exception here
    // connect to the locator using default port 10334
    ClientCache cache = new ClientCacheFactory().addPoolLocator("172.16.204.139", 10334)
        .set("log-level", "WARN").create();

    App example = new App();

    // create a local region that matches the server region
    ClientRegionFactory<Integer, String> clientRegionFactory =
        cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
    clientRegionFactory.addCacheListener(new ExampleCacheListener());
    Region<Integer, String> region = clientRegionFactory.create("exampleRegion");

    // 1. Put the entries and capture the keys we generated
    Collection<Integer> insertedKeys = example.putEntries(region);
    
    // 2. Sleep for 10 seconds
    System.out.println("⏳ Sleeping for 10 seconds. Check gfsh now: show metrics --region=/exampleRegion");
    Thread.sleep(10000); 

    // 3. Loop through our saved keys and delete them
    System.out.println("🗑️ Waking up! Deleting all " + insertedKeys.size() + " entries...");
    for (Integer key : insertedKeys) {
        region.destroy(key); // This will trigger the afterDestroy event in ExampleCacheListener!
    }
    System.out.println("✅ Deletion complete.");

    // Brief pause to ensure the listener finishes logging to the console before exit
    Thread.sleep(2000);

    cache.close();
  }

  private Collection<Integer> generateIntegers() {
    IntStream stream = new Random().ints(0, ITERATIONS);
    Iterator<Integer> iterator = stream.iterator();
    Collection<Integer> integers = new ArrayList<>();
    while (iterator.hasNext() && integers.size() < ITERATIONS) {
      Integer integer = iterator.next();
      if (!integers.contains(integer)) {
        integers.add(integer);
      }
    }
    return integers;
  }

  // <-- Changed from void to Collection<Integer> to return the keys
  public Collection<Integer> putEntries(Map<Integer, String> region) {
    Collection<Integer> integers = generateIntegers();
    Iterator<Integer> iterator = integers.iterator();

    while (iterator.hasNext()) {
      Integer integer = iterator.next();
      region.put(integer, integer.toString());
    }
    System.out.println("✅ Created " + integers.size() + " entries.");
    return integers; // <-- Return the keys so main() can destroy them later
  }
}
