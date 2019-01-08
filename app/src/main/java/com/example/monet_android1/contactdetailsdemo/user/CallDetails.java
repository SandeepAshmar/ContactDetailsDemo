package com.example.monet_android1.contactdetailsdemo.user;

import java.util.ArrayList;

public class CallDetails {

    private ArrayList<String> name = new ArrayList<>();

    private ArrayList<String> mobile = new ArrayList<>();

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.add(name);
    }

    public ArrayList<String> getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile.add(mobile);
    }
}
