package com.example.mac.cardbox.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserRelation  implements Serializable {
    User user_follow;
    User user_befollowed;
    Timestamp follow_time;
    public User getUser_follow() {
        return user_follow;
    }
    public void setUser_follow(User user_follow) {
        this.user_follow = user_follow;
    }
    public User getUser_befollowed() {
        return user_befollowed;
    }
    public void setUser_befollowed(User user_befollowed) {
        this.user_befollowed = user_befollowed;
    }
    public Timestamp getFollow_time() {
        return follow_time;
    }
    public void setFollow_time(Timestamp follow_time) {
        this.follow_time = follow_time;
    }



}

