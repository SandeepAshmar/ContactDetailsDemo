package com.example.monet_android1.contactdetailsdemo.appDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.monet_android1.contactdetailsdemo.dao.MyCallDao;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;

@Database(entities = {CallLog.class},version = 1)
public abstract class MyCallsAppDatabase extends RoomDatabase {

    public abstract MyCallDao myCallDao();

}
