package com.vwo.sampleapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;

/**
 * Created by aman on 09/08/17.
 */
public class Success implements Parcelable {
    private String message;
    @DrawableRes
    private int imageId;

    public Success(String message, @DrawableRes int imageId) {
        this.message = message;
        this.imageId = imageId;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeInt(this.imageId);
    }

    /**
     * Instantiates a new Success.
     */
    public Success() {
    }

    /**
     * Instantiates a new Success.
     *
     * @param in the in
     */
    protected Success(Parcel in) {
        this.message = in.readString();
        this.imageId = in.readInt();
    }

    /**
     * The constant CREATOR.
     */
    public static final Parcelable.Creator<Success> CREATOR = new Parcelable.Creator<Success>() {
        @Override
        public Success createFromParcel(Parcel source) {
            return new Success(source);
        }

        @Override
        public Success[] newArray(int size) {
            return new Success[size];
        }
    };
}
