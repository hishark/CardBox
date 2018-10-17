package com.example.mac.cardbox.util;


import com.example.mac.cardbox.bean.User;

public class CurrentUserUtil {
    public static User user;
    public static User getCurrentUser(){
        return user;
    }
    public static void setCurrentUser(User u){
        user = u;
    }
}
