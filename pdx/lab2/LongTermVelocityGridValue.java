import org.apache.geode.pdx.PdxSerializable;
import org.apache.geode.pdx.PdxWriter;
import org.apache.geode.pdx.PdxReader;

public class LongTermVelocityGridValue implements PdxSerializable {
    private String ltvValue;
    private long expiryTime;

    public LongTermVelocityGridValue() {}
    public LongTermVelocityGridValue(String ltvValue, long expiryTime) {
        this.ltvValue = ltvValue;
        this.expiryTime = expiryTime;
    }

    @Override
    public void toData(PdxWriter writer) {
        writer.writeString("ltvValue", ltvValue);
        writer.writeLong("expiryTime", expiryTime);
    }

    @Override
    public void fromData(PdxReader reader) {
        this.ltvValue = reader.readString("ltvValue");
        this.expiryTime = reader.readLong("expiryTime");
    }

    @Override
    public String toString() {
        return "LongTermVelocityGridValue{" +
                "ltvValue='" + ltvValue + '\'' +
                ", expiryTime=" + expiryTime +
                '}';
    }
}
