package com.vwo.sampleapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aman on 07/08/17.
 */
public class Item implements Parcelable {
    private String name;
    private String price;
    private boolean inStock;
    private boolean codAvailable;

    public Item(String name, String price, boolean inStock, boolean codAvailable) {
        this.name = name;
        this.price = price;
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

    /**
     * Gets price.
     *
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(String price) {
        this.price = price;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeByte(this.inStock ? (byte) 1 : (byte) 0);
        dest.writeByte(this.codAvailable ? (byte) 1 : (byte) 0);
    }

    protected Item(Parcel in) {
        this.name = in.readString();
        this.price = in.readString();
        this.inStock = in.readByte() != 0;
        this.codAvailable = in.readByte() != 0;
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
