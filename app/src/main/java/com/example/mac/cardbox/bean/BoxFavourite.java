package com.example.mac.cardbox.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class BoxFavourite implements Serializable {
    Box box;
    User user;
    Timestamp favourite_time;

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getFavourite_time() {
        return favourite_time;
    }

    public void setFavourite_time(Timestamp favourite_time) {
        this.favourite_time = favourite_time;
    }
}


