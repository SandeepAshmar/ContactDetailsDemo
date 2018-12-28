package com.example.monet_android1.contactdetailsdemo.appDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.monet_android1.contactdetailsdemo.dao.MyContactDao;
import com.example.monet_android1.contactdetailsdemo.user.Contacts;

@Database(entities = {Contacts.class},version = 1)
public abstract class MyContactAppDatabase extends RoomDatabase {

    public abstract MyContactDao myContactDao();

}
