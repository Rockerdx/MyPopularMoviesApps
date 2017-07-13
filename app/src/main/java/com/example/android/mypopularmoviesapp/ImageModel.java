package com.example.android.mypopularmoviesapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by acer on 6/15/2017.
 */

public class ImageModel implements Parcelable {

    String title;
    String overview;
    String releaseDate;
    String runtime;
    byte[] offlineImage;

    public byte[] getOfflineImage() {
        return offlineImage;
    }

    public void setOfflineImage(byte[] offlineImage) {
        this.offlineImage = offlineImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    String language;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    String rating;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    String imagePath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public ImageModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeString(this.runtime);
        dest.writeByteArray(this.offlineImage);
        dest.writeString(this.id);
        dest.writeString(this.language);
        dest.writeString(this.rating);
        dest.writeString(this.imagePath);
    }

    protected ImageModel(Parcel in) {
        this.title = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.runtime = in.readString();
        this.offlineImage = in.createByteArray();
        this.id = in.readString();
        this.language = in.readString();
        this.rating = in.readString();
        this.imagePath = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel source) {
            return new ImageModel(source);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };
}
