package com.example.monet_android1.contactdetailsdemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ViewPagerAdapter;
import com.example.monet_android1.contactdetailsdemo.appDatabase.MyCallsAppDatabase;
import com.example.monet_android1.contactdetailsdemo.fragment.CallDetailsFragment;
import com.example.monet_android1.contactdetailsdemo.fragment.ContactsFragment;
import com.example.monet_android1.contactdetailsdemo.fragment.OcrFragment;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.filterNumber;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.voiceSearch;

@SuppressLint("RestrictedApi")
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
    private CardView card_search;
    private ImageView search;
    private FloatingActionButton fab_main;
    private CoordinatorLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        card_search = findViewById(R.id.card_search);
        search = findViewById(R.id.img_mainSearch);
        fab_main = findViewById(R.id.fab_main);

        lp = (CoordinatorLayout.LayoutParams) fab_main.getLayoutParams();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceSearch(MainActivity.this);
            }
        });

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactsFragment(), "");
        adapter.addFragment(new CallDetailsFragment(), "");
        adapter.addFragment(new OcrFragment(), "");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#D81B60"));


        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 1){
                    lp.gravity = Gravity.CENTER;
                    fab_main.setLayoutParams(lp);
                    fab_main.setVisibility(View.VISIBLE);
                }else if (tab.getPosition() == 0){
                    lp.gravity = Gravity.RIGHT;
                    fab_main.setLayoutParams(lp);
                    fab_main.setVisibility(View.VISIBLE);
                }else{
                    fab_main.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                Toast.makeText(MainActivity.this, ""+tab.getCustomView(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:")));
            }
        });

        myCallsAppDatabase = Room.databaseBuilder(this, MyCallsAppDatabase.class, "calldb")
                .allowMainThreadQueries()
                .build();

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                callDetailsFragment.readFromDb(MainActivity.this);
            }
        };

        card_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchScreen.class));
            }
        });

        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_group_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_call_detail);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_general_ocr);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    String yourResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                    Intent intent = new Intent(this, SearchScreen.class);
                    intent.putExtra("searchResult", yourResult);
                    startActivity(intent);
                }
                break;
            }
        }
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
        contactList.getImageList().clear();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = filterNumber(phoneNumber);
            if (contactList.getName().size() == 0) {
                contactList.setName(name);
                contactList.setMobile(phoneNumber);
            } else if (!contactList.getMobile().contains(phoneNumber)) {
                contactList.setName(name);
                contactList.setMobile(phoneNumber);
            }
        }

        nameList.addAll(contactList.getName());
        mobileList.addAll(contactList.getMobile());
        contactList.getMobile().clear();
        Collections.sort(contactList.getName(), String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < contactList.getName().size(); i++) {
            String name = contactList.getName().get(i);
            for (int j = 0; j < nameList.size(); j++) {
                if (nameList.get(j).equals(name)) {
                    if (contactList.getMobile().size() == 0) {
                        contactList.setMobile(mobileList.get(j));
                    } else if (!contactList.getMobile().contains(mobileList.get(j))) {
                        contactList.setMobile(mobileList.get(j));
                    } else {
                        contactList.getName().remove(i);
                        break;
                    }
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
                Manifest.permission.WRITE_CONTACTS)+ ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        return result == PERMISSION_GRANTED;
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
                    Manifest.permission.WRITE_CONTACTS, Manifest.permission.CAMERA}, 1012);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] != PERMISSION_GRANTED) {
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