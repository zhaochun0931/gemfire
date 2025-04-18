import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import java.util.HashSet;
import java.util.Set;

public class Example {
  public static final String ARENDELLE = "Arendelle";
  public static final String BORDURIA = "Borduria";
  public static final String CASCADIA = "Cascadia";
  public static final String ELBONIA = "Elbonia";
  public static final String FLORIN = "Florin";
  public static final String GRAUSTARK = "Graustark";
  public static final String LATVERIA = "Latveria";
  public static final String MARKOVIA = "Markovia";
  public static final String PARADOR = "Parador";
  public static final String SIERRA_GORDO = "Sierra Gordo";
  final Region<String, Country> region;

  public Example(Region<String, Country> region) {
    this.region = region;
  }

  public static void main(String[] args) {
    // connect to the locator using default port 10334
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN")
        .setPdxSerializer(
            new ReflectionBasedAutoSerializer("Country"))
        .create();

    // create a local region that matches the server region
    Region<String, Country> region =
        cache.<String, Country>createClientRegionFactory(ClientRegionShortcut.PROXY)
            .create("example-region");

    Example example = new Example(region);
    example.insertValues();
    example.printValues(example.getKeys());

    cache.close();
  }

  Country create(String name) {
    return create(name, name + " City");
  }

  Country create(String name, String capitol) {
    return create(name, capitol, "");
  }

  Country create(String name, String capitol, String language) {
    return create(name, capitol, language, "", 0);
  }

  Country create(String name, String capitol, String language, String currency, int population) {
    return new Country(name, capitol, language, currency, population);
  }

  Set<String> getKeys() {
    return new HashSet<>(region.keySetOnServer());
  }

  void insertValues() {
    insertValue(create(ARENDELLE, "Arendelle City", "Arendellii", "Arendelle Krona", 76573));
    insertValue(create(BORDURIA, "Szohôd", "Bordurian", "Bordurian Dinar", 1000000));
    insertValue(create(CASCADIA, "Portland", "Pacific Northwest English", "United States Dollar",
        16029520));
    insertValue(create(ELBONIA));
    insertValue(create(FLORIN));
    insertValue(create(GRAUSTARK, "Edelweiss"));
    insertValue(create(LATVERIA, "Doomstadt", "Latverian", "Latverian Franc", 500000));
    insertValue(create(MARKOVIA, "Markovburg", "German"));
    insertValue(create(PARADOR));
    insertValue(create(SIERRA_GORDO, "Rio Lindo", "Spanish"));
  }

  void insertValue(Country country) {
    region.put(country.getName(), country);
  }

  void printValues(Set<String> keys) {
    for (String key : keys) {
      Country country = region.get(key);
      System.out.println(key + ": " + country);
    }
  }
}
