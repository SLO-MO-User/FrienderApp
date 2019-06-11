package com.example.frienderapp;

public class Friend {
    private String mNickname;
    private String mProfileImageURI;
    private String mEmail;
    private String mUID;

    Friend(String nickname, String email, String profileImageURI, String uid) {
        mNickname = nickname;
        mProfileImageURI = profileImageURI;
        mEmail = email;
        mUID = uid;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getProfileImageURI() {
        return mProfileImageURI;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUID() {
        return mUID;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "Name='" + mNickname + '\'' +
                ", ProfileImageURI='" + mProfileImageURI + '\'' +
                ", Email='" + mEmail + '\'' +
                '}';
    }
}
