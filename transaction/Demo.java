import org.apache.geode.cache.*;
import org.apache.geode.cache.client.*;

public class Demo {
  public static void main(String[] args) {
    ClientCache cache = null;
    try {
      // Connect to the GemFire cluster
      cache = new ClientCacheFactory()
          .addPoolLocator("localhost", 10334)
          .set("log-level", "info")
          .create();

      // Get or create a region
      Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
          .create("MyRegion");

      CacheTransactionManager txManager = cache.getCacheTransactionManager();

      // ---- DEMO 1: Successful Transaction Commit ----
      System.out.println("--- Starting commit demo ---");
      txManager.begin();
      try {
        System.out.println("Putting Key1 and Key2 inside transaction...");
        region.put("Key1", "Value1");
        region.put("Key2", "Value2");
        
        System.out.println("Committing transaction...");
        txManager.commit();
        System.out.println("Transaction committed successfully.");
      } catch (Exception e) {
        System.err.println("Commit failed: " + e.getMessage());
        txManager.rollback();
      }

      System.out.println("Post-commit check:");
      System.out.println("Value of Key1: " + region.get("Key1")); // Should be Value1
      System.out.println("Value of Key2: " + region.get("Key2")); // Should be Value2
      System.out.println("----------------------------");

      // ---- DEMO 2: Transaction Rollback ----
      System.out.println("--- Starting rollback demo ---");
      txManager.begin();
      try {
        System.out.println("Putting Key3 and Key4 inside transaction...");
        region.put("Key3", "Value3");
        region.put("Key4", "Value4");
        
        System.out.println("Rolling back transaction...");
        txManager.rollback();
        System.out.println("Transaction rolled back.");
      } catch (Exception e) {
        System.err.println("An unexpected error occurred: " + e.getMessage());
      }

      System.out.println("Post-rollback check:");
      System.out.println("Value of Key3: " + region.get("Key3")); // Should be null
      System.out.println("Value of Key4: " + region.get("Key4")); // Should be null
      System.out.println("----------------------------");
    } finally {
      if (cache != null) {
        cache.close();
      }
    }
  }
}
