package com.digitaldealsolution.plotlinetask.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ImageUpload implements Serializable, Parcelable {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("output_url")
    @Expose
    private String outputUrl;

    protected ImageUpload(Parcel in) {
        userId = in.readString();
        date = in.readString();
        imageUrl = in.readString();
        outputUrl = in.readString();
    }


    public static final Creator<ImageUpload> CREATOR = new Creator<ImageUpload>() {
        @Override
        public ImageUpload createFromParcel(Parcel in) {
            return new ImageUpload(in);
        }

        @Override
        public ImageUpload[] newArray(int size) {
            return new ImageUpload[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(date);
        dest.writeString(imageUrl);
        dest.writeString(outputUrl);
    }

    public ImageUpload(String userId, String date, String imageUrl, String outputUrl) {
        this.userId = userId;
        this.date = date;
        this.imageUrl = imageUrl;
        this.outputUrl = outputUrl;
    }
}
