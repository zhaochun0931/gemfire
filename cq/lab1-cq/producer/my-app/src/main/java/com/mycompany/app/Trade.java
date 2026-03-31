package com.mycompany.app;

import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializable;
import org.apache.geode.pdx.PdxWriter;

public class Trade implements PdxSerializable {
    private String id;
    private String ticker;
    private double price;

    // Mandatory empty constructor for Geode
    public Trade() {} 

    public Trade(String id, String ticker, double price) {
        this.id = id;
        this.ticker = ticker;
        this.price = price;
    }

    @Override
    public void toData(PdxWriter writer) {
        writer.writeString("id", id)
              .writeString("ticker", ticker)
              .writeDouble("price", price);
    }

    @Override
    public void fromData(PdxReader reader) {
        this.id = reader.readString("id");
        this.ticker = reader.readString("ticker");
        this.price = reader.readDouble("price");
    }

    @Override
    public String toString() {
        return "Trade{ticker='" + ticker + "', price=" + price + "}";
    }
}
