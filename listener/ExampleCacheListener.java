import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

public class ExampleCacheListener extends CacheListenerAdapter<Integer, String> {
  public ExampleCacheListener() {}

  @Override
  public void afterCreate(EntryEvent<Integer, String> event) {
    System.out.println("received create for key " + event.getKey());
  }

  @Override
  public void afterDestroy(EntryEvent<Integer, String> event) {
    System.out.println("received deletion for key " + event.getKey());
  }
}
