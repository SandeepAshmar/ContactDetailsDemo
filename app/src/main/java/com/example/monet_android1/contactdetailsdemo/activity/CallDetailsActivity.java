package com.example.monet_android1.contactdetailsdemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.BuildConfig;
import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.CallDetailsAdapter;
import com.example.monet_android1.contactdetailsdemo.adapter.CallLogAdapter;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;
import com.example.monet_android1.contactdetailsdemo.user.CircularImageView;
import com.example.monet_android1.contactdetailsdemo.user.UserCallDetails;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myCallsAppDatabase;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.callUser;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.checkUnsavedNumberOnWhatsapp;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.deleteContact;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.getContactIDFromNumber;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.getPhoto;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.hasWhatsApp;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.sendSMS;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.whatsApplicationCheck;

public class CallDetailsActivity extends AppCompatActivity {

    private ImageView back, call, color, add, msg, whatsapp;
    private TextView name, mobile, cardName, cardMobile, recent;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CallDetailsAdapter adapter;
    private String Name = "", Mobile = "", ColorName = "";
    private UserCallDetails userCallDetails = new UserCallDetails();
    private PopupMenu popupMenu;
    private CircularImageView circleImage;
    private LinearLayout ll_call;

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
        cardName = findViewById(R.id.tv_cardName);
        cardMobile = findViewById(R.id.tv_cardMobile);
        add = findViewById(R.id.img_Add);
        msg = findViewById(R.id.img_cardMsg);
        recent = findViewById(R.id.tv_recent);
        recyclerView = findViewById(R.id.rv_details);
        circleImage = findViewById(R.id.img_circleDetails);
        ll_call = findViewById(R.id.ll_call);
        whatsapp = findViewById(R.id.img_whatapp);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setActivityData();
        setAdapterData();
        handleClick();

        if(whatsApplicationCheck(this)){
            whatsapp.setVisibility(View.VISIBLE);
        }else{
            whatsapp.setVisibility(View.GONE);
        }

        if(Mobile.length() == 10){
            whatsapp.setVisibility(View.VISIBLE);
        }else{
            whatsapp.setVisibility(View.GONE);
        }

        circleImage.setImageBitmap(getPhoto(Mobile, this));

    }

    private void setActivityData() {
        Name = getIntent().getStringExtra("name");
        Mobile = getIntent().getStringExtra("mobile");
        ColorName = getIntent().getStringExtra("color").toUpperCase();
        color.setBackgroundColor(Color.parseColor(ColorName));
        recent.setTextColor(Color.parseColor(ColorName));

        popupMenu = new PopupMenu(this, add);

        if (!Name.isEmpty()) {
            if (checkNumberOnWhatsapp(Mobile)) {
                whatsapp.setVisibility(View.VISIBLE);
            } else {
                whatsapp.setVisibility(View.GONE);
            }
        }

        if (Name.isEmpty()) {
            name.setText(Mobile);
        } else {
            name.setText(Name);
        }

        cardMobile.setText(Mobile);

    }

    private void handleClick() {
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Name.isEmpty()) {
                    checkUnsavedNumberOnWhatsapp(CallDetailsActivity.this, Mobile);
                } else {
                    if (checkNumberOnWhatsapp(Mobile)) {
                        sendMsgOnSavedWhatsappNumber(Mobile);
                    } else {
                        Toast.makeText(CallDetailsActivity.this,
                                "This mobile no does not have whatsapp account", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUser(Mobile, CallDetailsActivity.this);
            }
        });

        ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUser(Mobile, CallDetailsActivity.this);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(Mobile, Name);
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendSMS(Mobile, CallDetailsActivity.this);
                } catch (Exception e) {
                    Toast.makeText(CallDetailsActivity.this, "Oops! something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAdapterData() {
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

    private boolean checkNumberOnWhatsapp(String mobile) {
        String id = String.valueOf(getContactIDFromNumber(mobile, CallDetailsActivity.this));
        if (hasWhatsApp(id, CallDetailsActivity.this) == 1) {
            return true;
        } else {
            return false;
        }

    }

    private void sendMsgOnSavedWhatsappNumber(String mobile) {
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        sendIntent.putExtra("jid",
                PhoneNumberUtils.stripSeparators("91" + mobile) + "@s.whatsapp.net");
        startActivity(sendIntent);
    }

    private void showMenu(final String number, final String name) {
        popupMenu.getMenu().clear();
        popupMenu.getMenu().add("Call");
        popupMenu.getMenu().add("Send message");
        popupMenu.getMenu().add("Edit number before call");
        if (name.isEmpty()) {
            popupMenu.getMenu().add("Add to contact");
        } else {
            popupMenu.getMenu().add("delete contact");
        }
        popupMenu.getMenu().add("Share contact");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals("Add to contact")) {
                    addNewContact(number);
                } else if (item.getTitle().equals("Call")) {
                    callUser(number, CallDetailsActivity.this);
                } else if (item.getTitle().equals("Send message")) {
                    sendSMS(number, CallDetailsActivity.this);
                } else if (item.getTitle().equals("delete contact")) {
                    deleteDialog();
                } else if (item.getTitle().equals("Share contact")) {
                    shareContact();
                } else if (item.getTitle().equals("Edit number before call")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                    startActivity(intent);
                }

                return true;
            }
        });
        popupMenu.show();
    }

    private void shareContact() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = Name + " " + Mobile;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CallDetailsActivity.this
                , R.style.DialogTheme);

        builder.setMessage("Are you sure, you want to delete " + Name + " from your contact list");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (deleteContact(CallDetailsActivity.this, Mobile, Name)) {
                    Toast.makeText(CallDetailsActivity.this, "Contact has been deleted from your contact list",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CallDetailsActivity.this, "Oops! something went wrong", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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

}
