package com.example.frienderapp;

public class Friend {
    private String mNickname;
    private String mProfileImageURI;
    private String mEmail;

    Friend(String nickname, String email, String profileImageURI) {
        mNickname = nickname;
        mProfileImageURI = profileImageURI;
        mEmail = email;
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

    @Override
    public String toString() {
        return "Friend{" +
                "Name='" + mNickname + '\'' +
                ", ProfileImageURI='" + mProfileImageURI + '\'' +
                ", Email='" + mEmail + '\'' +
                '}';
    }
}
