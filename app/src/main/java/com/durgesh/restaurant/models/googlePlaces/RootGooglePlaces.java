package com.durgesh.restaurant.models.googlePlaces;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Prajakta Patil on 4/9/17.

 */

@Getter
@Setter
public class RootGooglePlaces implements Parcelable {

    public static final Creator<RootGooglePlaces> CREATOR = new Creator<RootGooglePlaces>() {
        @Override
        public RootGooglePlaces createFromParcel(Parcel source) {
            return new RootGooglePlaces(source);
        }

        @Override
        public RootGooglePlaces[] newArray(int size) {
            return new RootGooglePlaces[size];
        }
    };
    private String next_page_token;
    private List<Result> results;
    private List<String> html_attributions;
    private String status;

    public RootGooglePlaces() {
    }

    public RootGooglePlaces(Parcel in) {
        this.next_page_token = in.readString();
        this.results = new ArrayList<Result>();
        in.readList(this.results, Result.class.getClassLoader());
        this.html_attributions = in.createStringArrayList();
        this.status = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.next_page_token);
        dest.writeList(this.results);
        dest.writeStringList(this.html_attributions);
        dest.writeString(this.status);
    }
}