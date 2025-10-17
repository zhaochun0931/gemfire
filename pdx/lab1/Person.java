import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializable;
import org.apache.geode.pdx.PdxWriter;

public class Person implements PdxSerializable {
    private String id;
    private String name;
    private int age;

    // Required no-arg constructor
    public Person() {}

    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public void toData(PdxWriter writer) {
        writer.writeString("id", id);
        writer.writeString("name", name);
        writer.writeInt("age", age);
    }

    @Override
    public void fromData(PdxReader reader) {
        id = reader.readString("id");
        name = reader.readString("name");
        age = reader.readInt("age");
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return String.format("Person{id='%s', name='%s', age=%d}", id, name, age);
    }
}
