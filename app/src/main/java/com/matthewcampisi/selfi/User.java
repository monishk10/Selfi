package com.matthewcampisi.selfi;

public class User {

    public String userDisplayName;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userDisplayName, String email) {
        this.userDisplayName = userDisplayName;
        this.email = email;
    }

}