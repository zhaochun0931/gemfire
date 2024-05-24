import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.TypeMismatchException;

public class Example {
  static String REGIONNAME = "example-region";
  static String NON_INDEXED_QUERY = "SELECT DISTINCT * FROM /" + REGIONNAME;
  static String TOP_LEVEL_INDEX_QUERY =
      "SELECT DISTINCT * FROM /" + REGIONNAME + " p WHERE p.name LIKE $1";
  static String NESTED_INDEX_QUERY =
      "SELECT DISTINCT * FROM /" + REGIONNAME + " p WHERE p.flight.airlineCode=$1";

  public static void main(String[] args) {
    // connect to the locator using default port 10334
    ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
        .set("log-level", "WARN").create();

    Example example = new Example();

    // create a local region that matches the server region
    ClientRegionFactory<String, Passenger> clientRegionFactory =
        cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
    Region<String, Passenger> region = clientRegionFactory.create("example-region");
    QueryService queryService = cache.getQueryService();

    RegionPopulator populator = new RegionPopulator();
    populator.populateRegion(region);

    System.out.println("Total number of passengers: "
        + example.countResults(queryService, NON_INDEXED_QUERY, new Object[] {}));
    for (String lastName : populator.lastNames) {
      System.out.println("Flights for " + lastName + ": " + example.countResults(queryService,
          TOP_LEVEL_INDEX_QUERY, new Object[] {"%" + lastName}));
    }
    for (String airline : populator.airlines) {
      System.out.println("Flights for " + airline + ": "
          + example.countResults(queryService, NESTED_INDEX_QUERY, new Object[] {airline}));
    }

    cache.close();
  }

  int countResults(QueryService queryService, String queryString, Object[] params) {
    try {
      int count = 0;
      SelectResults<Passenger> results =
          (SelectResults<Passenger>) queryService.newQuery(queryString).execute(params);
      for (Passenger passenger : results) {
        ++count;
      }
      return count;
    } catch (FunctionDomainException | TypeMismatchException | NameResolutionException
        | QueryInvocationTargetException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
