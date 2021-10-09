package com.weizu.myapplication.Bean;

public class User {
    private String name;
    private String userID;

    public User(String name, String userid) {
        this.name = name;
        this.userID = userid;
    }

    public String getName() {
        return name;
    }

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

