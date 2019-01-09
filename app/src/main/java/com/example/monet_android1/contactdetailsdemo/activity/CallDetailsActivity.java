package com.example.monet_android1.contactdetailsdemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.CallDetailsAdapter;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;
import com.example.monet_android1.contactdetailsdemo.user.UserCallDetails;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myCallsAppDatabase;

public class CallDetailsActivity extends AppCompatActivity {

    private ImageView back, call, color, add, msg;
    private TextView name, mobile, cardName, cardMobile, recent, whatsName, whatsMobile;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CallDetailsAdapter adapter;
    private String Name = "", Mobile = "", ColorName = "";
    private UserCallDetails userCallDetails = new UserCallDetails();
    private CardView whatsappCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_details);

        initView();
    }

    private void initView() {
        back = findViewById(R.id.img_back);
        call = findViewById(R.id.img_cardCall);
        color = findViewById(R.id.img_color);
        name = findViewById(R.id.tv_detName);
        mobile = findViewById(R.id.tv_detMobile);
        cardName = findViewById(R.id.tv_cardName);
        cardMobile = findViewById(R.id.tv_cardMobile);
        add = findViewById(R.id.img_Add);
        msg = findViewById(R.id.img_cardMsg);
        recent = findViewById(R.id.tv_recent);
        whatsappCard = findViewById(R.id.whatsappCard);
        whatsName = findViewById(R.id.tv_whatsAppName);
        whatsMobile= findViewById(R.id.tv_whatsAppMobile);

        Name = getIntent().getStringExtra("name");
        Mobile = getIntent().getStringExtra("mobile");
        ColorName = getIntent().getStringExtra("color").toUpperCase();
        color.setBackgroundColor(Color.parseColor(ColorName));
        recent.setTextColor(Color.parseColor(ColorName));

        if(getWhatsAppNumbers(Name).isEmpty()){
            whatsappCard.setVisibility(View.GONE);
        }else{
            whatsName.setText(Name);
            whatsMobile.setText(Mobile);
        }

        whatsappCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone="+
                            "+91 "+Mobile +"&text=" + URLEncoder.encode("", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(CallDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Name.isEmpty()) {
            cardName.setText("Add New Contact");
            name.setText("Add New Contact");
            add.setVisibility(View.VISIBLE);
        } else {
            name.setText(Name);
            cardName.setText(Name);
            add.setVisibility(View.GONE);
        }

        mobile.setText(Mobile);
        cardMobile.setText(Mobile);

        recyclerView = findViewById(R.id.rv_details);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Mobile));
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContact(Mobile);
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendSMS(Mobile);
                } catch (Exception e) {
                    Toast.makeText(CallDetailsActivity.this, "Oops! something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        List<CallLog> callLogs = myCallsAppDatabase.myCallDao().getCallDetials();
        userCallDetails.getMobile().clear();
        userCallDetails.getTime().clear();
        userCallDetails.getType().clear();
        for (int i = 0; i < callLogs.size(); i++) {
            if (callLogs.get(i).getMobile().contains(Mobile)) {
                userCallDetails.setMobile(callLogs.get(i).getMobile());
                userCallDetails.setTime(callLogs.get(i).getTime());
                userCallDetails.setType(callLogs.get(i).getCallType());
            }
        }
        Collections.reverse(userCallDetails.getMobile());
        Collections.reverse(userCallDetails.getTime());
        Collections.reverse(userCallDetails.getType());
        if (userCallDetails.getMobile().size() == 0) {
            userCallDetails.setMobile("No data");
        }
        adapter = new CallDetailsAdapter(this, userCallDetails);
        recyclerView.setAdapter(adapter);
        recyclerView.hasFixedSize();
    }

    public String getWhatsAppNumbers(String contactName) {
        String phoneNumber = "";
        Cursor cursor1 = getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ? AND " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY + " = ?",
                new String[]{"com.whatsapp", contactName},
                null);

        while (cursor1.moveToNext()) {
            String rawContactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.RawContacts._ID));

            Cursor cursor2 = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data.DATA3},
                    ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.Data.RAW_CONTACT_ID + " = ? ",
                    new String[]{"vnd.android.cursor.item/vnd.com.whatsapp.profile", rawContactId},
                    null);

            while (cursor2.moveToNext()) {
                phoneNumber = cursor2.getString(0);

                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                if (phoneNumber.startsWith("Message "))
                    phoneNumber = phoneNumber.replace("Message ", "");
            }
        }

        return phoneNumber;
    }

    public void addNewContact(String number) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent
                .putExtra(ContactsContract.Intents.Insert.PHONE, number);
        startActivityForResult(contactIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void sendSMS(String mobile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra("address", mobile);
            sendIntent.setType("text/plain");

            if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            startActivity(sendIntent);

        } else // For early versions, do what worked for you before.
        {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", mobile);
            startActivity(smsIntent);
        }
    }

}
