package com.example.monet_android1.contactdetailsdemo.user;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ContactList {

    private ArrayList<String> name = new ArrayList<>();

    private ArrayList<String> mobile = new ArrayList<>();

    private ArrayList<Bitmap> imageList = new ArrayList<>();

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(String nameS) {
        name.add(nameS);
    }

    public ArrayList<String> getMobile() {
        return mobile;
    }

    public void setMobile(String mobileS) {
        this.mobile.add(mobileS);
    }

    public ArrayList<Bitmap> getImageList() {
        return imageList;
    }

    public void setImageList(Bitmap image) {
        this.imageList.add(image);
    }
}
