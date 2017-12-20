package com.durgesh.restaurant.models.googlePlaces;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;


/**
 * Created by Snehal Tembare on 30/8/17.

 */

@Setter
@Getter

public class Result implements Parcelable{

    private String icon;
    private String id;
    private String name;
    private double rating;
    private Geometry geometry;
    private Integer price_level;
    private OpeningHours opening_hours;
    private String reference;
    private ArrayList<Photo> photos;

    protected Result(Parcel in) {
        icon = in.readString();
        id = in.readString();
        name = in.readString();
        rating = in.readDouble();
        geometry = in.readParcelable(Geometry.class.getClassLoader());
        opening_hours = in.readParcelable(OpeningHours.class.getClassLoader());
        price_level = in.readInt();
        reference = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icon);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(rating);
        dest.writeParcelable(geometry,flags);
        dest.writeParcelable(opening_hours,flags);
        dest.writeInt(price_level);
        dest.writeString(reference);
        dest.writeList(photos);
    }
}
