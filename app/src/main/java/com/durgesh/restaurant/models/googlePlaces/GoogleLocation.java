package com.durgesh.restaurant.models.googlePlaces;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Prajakta Patil on 4/9/17.
 * Copyright © 2017 Synerzip. All rights reserved
 */
@Setter
@Getter
public class GoogleLocation implements Parcelable {

    public static final Creator<GoogleLocation> CREATOR = new Creator<GoogleLocation>() {
        @Override
        public GoogleLocation createFromParcel(Parcel source) {
            return new GoogleLocation(source);
        }

        @Override
        public GoogleLocation[] newArray(int size) {
            return new GoogleLocation[size];
        }
    };
    private String lng;
    private String lat;

    public GoogleLocation() {
    }

    protected GoogleLocation(Parcel in) {
        this.lng = in.readString();
        this.lat = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lng);
        dest.writeString(this.lat);
    }
}
