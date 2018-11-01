package com.example.mac.cardbox.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserMessage implements Serializable {
    User user_sender;
    User user_receiver;
    String message_content;
    Timestamp message_send_time;

    public User getUser_sender() {
        return user_sender;
    }
    public void setUser_sender(User user_sender) {
        this.user_sender = user_sender;
    }
    public User getUser_receiver() {
        return user_receiver;
    }
    public void setUser_receiver(User user_receiver) {
        this.user_receiver = user_receiver;
    }
    public String getMessage_content() {
        return message_content;
    }
    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }
    public Timestamp getMessage_send_time() {
        return message_send_time;
    }
    public void setMessage_send_time(Timestamp message_send_time) {
        this.message_send_time = message_send_time;
    }



}
