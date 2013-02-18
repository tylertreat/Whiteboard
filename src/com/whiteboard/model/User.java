package com.whiteboard.model;

public class User {

    private long mId;
    private String mName;
    private String mEmail;
    private String mPassword;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getDisplayName() {
        if (mName != null)
            return mName;
        return mEmail;
    }

}
