package com.durgesh.restaurant.models.googlePlaces;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Snehal Tembare on 31/8/17.

 */
@Getter
@Setter

public class Place implements Parcelable {
    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
    private String name;
    private double rating;
    private ArrayList<Double> distanceArrayList;
    private Float distance;
    private String photo;
    private OpeningHours openingHours;

    protected Place(Parcel in) {
        name = in.readString();
        rating = in.readDouble();
        distance = in.readFloat();
        photo = in.readString();
        openingHours = in.readParcelable(OpeningHours.class.getClassLoader());
    }

    public Place(String name,
                 double rating,
                 ArrayList<Double> distanceArrayList,
                 Float distance,
                 String photo,
    OpeningHours openingHour) {
        this.name = name;
        this.rating = rating;
        this.distanceArrayList = distanceArrayList;
        this.distance = distance;
        this.photo = photo;
        this.openingHours = openingHour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(rating);
        dest.writeFloat(distance);
        dest.writeString(photo);
        dest.writeParcelable(openingHours,flags);
    }
}
