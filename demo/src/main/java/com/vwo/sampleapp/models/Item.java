package com.vwo.sampleapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aman on 07/08/17.
 */
public class Item implements Parcelable {
    private String name;
    private int price;
    private String units;
    private boolean inStock;
    private boolean codAvailable;

    public Item(String name, int price, String units, boolean inStock, boolean codAvailable) {
        this.name = name;
        this.price = price;
        this.units = units;
        this.inStock = inStock;
        this.codAvailable = codAvailable;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    protected Item(Parcel in) {
        this.name = in.readString();
        this.price = in.readInt();
        this.units = in.readString();
        this.inStock = in.readByte() != 0;
        this.codAvailable = in.readByte() != 0;
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    public String getUnits() {
        return units;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public boolean isCodAvailable() {
        return codAvailable;
    }

    public void setCodAvailable(boolean codAvailable) {
        this.codAvailable = codAvailable;
    }

    /**
     * Instantiates a new Item.
     */
    public Item() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.price);
        dest.writeString(this.units);
        dest.writeByte(this.inStock ? (byte) 1 : (byte) 0);
        dest.writeByte(this.codAvailable ? (byte) 1 : (byte) 0);
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
