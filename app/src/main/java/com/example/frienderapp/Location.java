package com.example.frienderapp;

public class Location implements java.io.Serializable {

    public String mLongitude;
    public String mLatitude;
    public String mDate;
    public String mTime;
    public String mCity;

    Location(String longitude, String latitude, String date, String time, String city) {
        mLongitude = longitude;
        mLatitude = latitude;
        mDate = date;
        mTime = time;
        mCity = city;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public String getCity() {
        return mCity;
    }

    @Override
    public String toString() {
        return "Location{" +
                "mLongitude='" + mLongitude + '\'' +
                ", mLatitude='" + mLatitude + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mTime='" + mTime + '\'' +
                ", mCity='" + mCity + '\'' +
                '}';
    }
}
