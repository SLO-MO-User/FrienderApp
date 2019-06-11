package com.example.frienderapp;

public class Messages implements Comparable {
    public String mDate;
    private String mMessage;
    private String mUser;

    public Messages(String message, String date, String user) {
        mMessage = message;
        mDate = date;
        mUser = user;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getDate() {
        return mDate;
    }

    public String getUser() {
        return mUser;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "mMessage='" + mMessage + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mUser='" + mUser + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return this.mDate.compareTo(((Messages) o).getDate());
    }

    /*public int compareTo(Messages m) {
        return this.mDate.compareTo(m.getDate());
    }*/
}
