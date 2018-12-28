package com.example.monet_android1.contactdetailsdemo.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.monet_android1.contactdetailsdemo.user.Contacts;

import java.util.List;

@Dao
public interface MyContactDao {

    @Insert
    public void addContactUser(Contacts contacts);

    @Query("select * from contacts")
    public List<Contacts> getContactUsers();

    @Delete
    public void deleteContactUser(Contacts contacts);

    @Update
    public void updateContactUser(Contacts contacts);
}
