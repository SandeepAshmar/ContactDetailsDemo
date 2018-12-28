package com.example.monet_android1.contactdetailsdemo.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.monet_android1.contactdetailsdemo.user.CallLog;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MyCallDao {

    @Insert
    public void addCallDetails(CallLog callLog);

    @Query("select * from calls")
    public List<CallLog> getCallDetials();
}
