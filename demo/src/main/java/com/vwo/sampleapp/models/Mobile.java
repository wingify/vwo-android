package com.vwo.sampleapp.models;

import android.os.Parcel;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;

/**
 * Created by aman on 07/08/17.
 */
public class Mobile extends Item {
    @DrawableRes
    private int imageId;
    private String vendor;
    private String variantDetails;
    private int rating;

    /**
     * Instantiates a new Mobile.
     *
     * @param name    the name
     * @param price   the price
     * @param imageId the image id
     * @param vendor  the vendor
     */
    public Mobile(String name, String price, boolean inStock, boolean codAvailable, int imageId,
                  String vendor, String variantDetails, @IntRange(from=0, to=5) int rating) {
        super(name, price, inStock, codAvailable);
        this.imageId = imageId;
        this.vendor = vendor;
        this.variantDetails = variantDetails;
        this.rating = rating;
    }

    /**
     * Gets image id.
     *
     * @return the image id
     */
    @DrawableRes
    public int getImageId() {
        return imageId;

    }

    /**
     * Sets image id.
     *
     * @param imageId the image id
     */
    public void setImageId(@DrawableRes int imageId) {
        this.imageId = imageId;
    }

    /**
     * Gets vendor.
     *
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets vendor.
     *
     * @param vendor the vendor
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVariantDetails() {
        return variantDetails;
    }

    public void setVariantDetails(String variantDetails) {
        this.variantDetails = variantDetails;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Instantiates a new Mobile.
     */
    public Mobile() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.imageId);
        dest.writeString(this.vendor);
        dest.writeString(this.variantDetails);
        dest.writeInt(this.rating);
    }

    protected Mobile(Parcel in) {
        super(in);
        this.imageId = in.readInt();
        this.vendor = in.readString();
        this.variantDetails = in.readString();
        this.rating = in.readInt();
    }

    public static final Creator<Mobile> CREATOR = new Creator<Mobile>() {
        @Override
        public Mobile createFromParcel(Parcel source) {
            return new Mobile(source);
        }

        @Override
        public Mobile[] newArray(int size) {
            return new Mobile[size];
        }
    };
}
