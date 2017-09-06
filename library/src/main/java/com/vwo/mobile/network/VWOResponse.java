package com.vwo.mobile.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aman on 05/09/17.
 */

public class VWOResponse implements Parcelable {
    private String data;
    private int responseCode;

    public VWOResponse(String data, int responseCode) {
        this.data = data;
        this.responseCode = responseCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data);
        dest.writeInt(this.responseCode);
    }

    protected VWOResponse(Parcel in) {
        this.data = in.readString();
        this.responseCode = in.readInt();
    }

    public static final Parcelable.Creator<VWOResponse> CREATOR = new Parcelable.Creator<VWOResponse>() {
        @Override
        public VWOResponse createFromParcel(Parcel source) {
            return new VWOResponse(source);
        }

        @Override
        public VWOResponse[] newArray(int size) {
            return new VWOResponse[size];
        }
    };
}
