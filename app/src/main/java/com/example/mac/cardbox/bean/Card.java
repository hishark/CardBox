package com.example.mac.cardbox.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class Card implements Serializable {
    String card_front;
    String card_back;
    String card_id;
    Box box;
    String card_type;
    Timestamp card_create_time;
    String card_marktype;


    public String getCard_front() {
        return card_front;
    }

    public void setCard_front(String card_front) {
        this.card_front = card_front;
    }

    public String getCard_back() {
        return card_back;
    }

    public void setCard_back(String card_back) {
        this.card_back = card_back;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public Timestamp getCard_create_time() {
        return card_create_time;
    }

    public void setCard_create_time(Timestamp card_create_time) {
        this.card_create_time = card_create_time;
    }

    public String getCard_marktype() {
        return card_marktype;
    }

    public void setCard_marktype(String card_marktype) {
        this.card_marktype = card_marktype;
    }
}
