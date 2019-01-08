package com.example.monet_android1.contactdetailsdemo.user;

import java.util.ArrayList;

public class UserCallDetails {

    private ArrayList<String> mobile = new ArrayList<>();
    private ArrayList<String> type = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();

    public ArrayList<String> getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile.add(mobile);
    }

    public ArrayList<String> getType() {
        return type;
    }

    public void setType(String type) {
        this.type.add(type);
    }

    public ArrayList<String> getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time.add(time);
    }
}
