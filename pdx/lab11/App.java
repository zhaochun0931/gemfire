import org.apache.geode.cache.*;
import org.apache.geode.cache.client.*;
import org.apache.geode.pdx.*;

public class App {
    public static void main(String[] args) {
        ClientCache cache = new ClientCacheFactory()
            .addPoolLocator("localhost", 10334)   // change host/port
            .setPdxReadSerialized(false)          // store as PDX
            .create();

        Region<String, Person> region = cache
            .<String, Person>createClientRegionFactory(ClientRegionShortcut.PROXY)
            .create("People");

        // When you put this object, GemFire auto-registers its PDX type
        Person p = new Person("Alice", 30);
        region.put("1", p);

        System.out.println("Inserted: " + p);
    }

    public static class Person implements PdxSerializable {
        private String name;
        private int age;

        public Person() {}
        public Person(String name, int age) {
            this.name = name; this.age = age;
        }

        @Override
        public void toData(PdxWriter writer) {
            writer.writeString("name", name);
            writer.writeInt("age", age);
        }

        @Override
        public void fromData(PdxReader reader) {
            this.name = reader.readString("name");
            this.age = reader.readInt("age");
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
}
