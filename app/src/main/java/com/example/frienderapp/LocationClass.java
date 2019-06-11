package com.example.frienderapp;

public class LocationClass implements java.io.Serializable {

    private String mLongitude;
    private String mLatitude;
    private String mDate;
    private String mTime;
    private String mCity;

    LocationClass(String longitude, String latitude, String date, String time, String city) {
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
