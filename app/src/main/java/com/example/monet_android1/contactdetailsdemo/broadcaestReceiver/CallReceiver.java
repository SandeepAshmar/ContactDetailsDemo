package com.example.monet_android1.contactdetailsdemo.broadcaestReceiver;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.appDatabase.MyCallsAppDatabase;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
    public static MyCallsAppDatabase myCallsAppDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            onCallStateChanged(context, state, number, intent);
        }
        int whichSim = intent.getIntExtra("simSlot",-1);
//        int whichSim =intent.getIntExtra("com.android.phone.extra.slot",-1);
        if(whichSim==0){
            Toast.makeText(context, "sim one", Toast.LENGTH_SHORT).show();
        }else if(whichSim == 1){
            Toast.makeText(context, "sim two", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "pta nahi", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCallStateChanged(Context context, int state, String number, Intent intent) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                onIncomingCallStarted(context, number, callStartTime, intent);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, number, callStartTime, intent);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, number, callStartTime, intent);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, number, callStartTime, new Date(), intent);
                } else {
                    onOutgoingCallEnded(context, number, callStartTime, new Date(), intent);
                }
                break;
        }
        lastState = state;
        Intent intent1 = new Intent("CallApp");
        context.sendBroadcast(intent1);
    }

    protected void onIncomingCallStarted(Context ctx, String number, Date start, Intent intent) {
//        Toast.makeText(ctx, "calling from " + number, Toast.LENGTH_SHORT).show();
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start, Intent intent) {
//        Toast.makeText(ctx, "calling to " + number, Toast.LENGTH_SHORT).show();
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end, Intent intent) {
//        Toast.makeText(ctx, "calling from " + number + " ended ", Toast.LENGTH_SHORT).show();
        saveData(ctx, number, intent, "Incoming Call");
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, Intent intent) {
//        Toast.makeText(ctx, "calling to " + number + " ended ", Toast.LENGTH_SHORT).show();
        saveData(ctx, number, intent, "Outgoing Call");
    }

    protected void onMissedCall(Context ctx, String number, Date start, Intent intent) {
//        Toast.makeText(ctx, "missed call from " + number + " sim ", Toast.LENGTH_SHORT).show();
        saveData(ctx, number, intent, "Missed Call");
    }

    @SuppressLint("ServiceCast")
    protected void saveData(Context ctx, String number, Intent intent, String callType) {

        myCallsAppDatabase = Room.databaseBuilder(ctx, MyCallsAppDatabase.class, "calldb")
                .allowMainThreadQueries()
                .build();

        number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        number = number.replace(" ", "");
        number = number.replace("-", "");
        number = number.replace("(", "");
        number = number.replace(")", "");
        number = number.replace("+91", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aaa");
        String dateString = dateFormat.format(new Date(System.currentTimeMillis()));
        CallLog callLog = new CallLog();
        if(number == null){
            callLog.setMobile("112");
        }else{
            callLog.setMobile(number);
        }

        callLog.setCallType(callType);
        callLog.setTime(dateString);
        myCallsAppDatabase.myCallDao().addCallDetails(callLog);
    }
}
