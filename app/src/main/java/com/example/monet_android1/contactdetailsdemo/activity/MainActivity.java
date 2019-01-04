package com.example.monet_android1.contactdetailsdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ViewPagerAdapter;
import com.example.monet_android1.contactdetailsdemo.appDatabase.MyCallsAppDatabase;
import com.example.monet_android1.contactdetailsdemo.fragment.CallDetailsFragment;
import com.example.monet_android1.contactdetailsdemo.fragment.ContactsFragment;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public static MyCallsAppDatabase myCallsAppDatabase;
    public static BroadcastReceiver br;
    public static ContactList contactList = new ContactList();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> mobileList = new ArrayList<>();
    private CallDetailsFragment callDetailsFragment = new CallDetailsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CallDetailsFragment(), "Call log");
        adapter.addFragment(new ContactsFragment(), "Contacts");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
//                Toast.makeText(MainActivity.this, "" + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));

        myCallsAppDatabase = Room.databaseBuilder(this, MyCallsAppDatabase.class, "calldb")
                .allowMainThreadQueries()
                .build();

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                callDetailsFragment.readFromDb(MainActivity.this);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPhoneStatePermission()) {
            getContactList();
        } else {
            requestPhoneStatePermission();
        }
        registerReceiver(br, new IntentFilter("CallApp"));
    }

    private void getContactList() {
        contactList.getName().clear();
        contactList.getMobile().clear();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            nameList.add(name);
            contactList.setName(name);
            mobileList.add(phoneNumber);
        }
        HashSet<String> nameHashSet = new HashSet<String>();
        nameHashSet.addAll(contactList.getName());
        contactList.getName().clear();
        contactList.getName().addAll(nameHashSet);
        Collections.sort(contactList.getName(), String.CASE_INSENSITIVE_ORDER);

        for (int i = 0; i < contactList.getName().size(); i++) {
            String name = contactList.getName().get(i);
            for (int j = 0; j < nameList.size(); j++) {
                if(nameList.get(j).equals(name)){
                    contactList.setMobile(mobileList.get(j));
                    break;
                }
            }
        }

        phones.close();
        nameList.clear();
        mobileList.clear();
    }

    private boolean checkPhoneStatePermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) + ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) + ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) + ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (this, Manifest.permission.READ_PHONE_STATE)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme);
            alertDialog.setMessage("You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS}, 1012);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme);
            alertDialog.setMessage("You Have To Give Permission From Your Device Setting To go in Setting Please Click on Settings Button");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        } else {

        }

    }
}