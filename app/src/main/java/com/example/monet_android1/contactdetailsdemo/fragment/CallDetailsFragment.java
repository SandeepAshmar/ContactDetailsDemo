package com.example.monet_android1.contactdetailsdemo.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.activity.MainActivity;
import com.example.monet_android1.contactdetailsdemo.adapter.CallLogAdapter;
import com.example.monet_android1.contactdetailsdemo.click.CallClickListner;
import com.example.monet_android1.contactdetailsdemo.user.CallDetails;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.contactList;
import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myCallsAppDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallDetailsFragment extends Fragment {

    private static RecyclerView rv_calls;
    private GridLayoutManager layoutManager;
    private static CallLogAdapter adapter;
    private static TextView tv_noCalls;
    private boolean isReverse = true;
    private CallDetails callDetails = new CallDetails();

    private CallClickListner callClickListner = new CallClickListner() {
        @Override
        public void onItemClick(String number) {
            addNewContact(number);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call_details, container, false);

        rv_calls = view.findViewById(R.id.rv_calls);
        tv_noCalls = view.findViewById(R.id.tv_noCalls);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        rv_calls.setLayoutManager(layoutManager);
        readFromDb(getActivity());

        return view;
    }

    public void readFromDb(Context context) {
        callDetails.getMobile().clear();
        callDetails.getName().clear();
        try {
            List<CallLog> callLogs = myCallsAppDatabase.myCallDao().getCallDetials();
            for (int i = 0; i < callLogs.size(); i++) {
                String allMobile = callLogs.get(i).getMobile();
                allMobile = allMobile.replace(" ", "");
                allMobile = allMobile.replace("-", "");
                allMobile = allMobile.replace("(", "");
                allMobile = allMobile.replace(")", "");
                allMobile = allMobile.replace("+91", "");
                if (callDetails.getMobile().size() == 0 && callDetails.getName().size() == 0) {
                    getName(allMobile);
                    callDetails.setMobile(callLogs.get(i).getMobile());
                } else {
                    if (!(callDetails.getMobile().contains(allMobile))) {
                        getName(allMobile);
                        callDetails.setMobile(callLogs.get(i).getMobile());
                    }

                }

            }

            if(isReverse){
                Collections.reverse(callDetails.getMobile());
                Collections.reverse(callDetails.getName());
                isReverse = false;
            }
            adapter = new CallLogAdapter(context, callDetails, callClickListner);
            rv_calls.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (callLogs.size() > 0) {
                rv_calls.setVisibility(View.VISIBLE);
                tv_noCalls.setVisibility(View.GONE);
            } else {
                rv_calls.setVisibility(View.GONE);
                tv_noCalls.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    protected void getName(String number) {

        String name = "";
        for (int i = 0; i < contactList.getMobile().size(); i++) {
            String mobile = contactList.getMobile().get(i);
            if (mobile.equals(number)) {
                name = contactList.getName().get(i);
            }
        }
        callDetails.setName(name);
    }

    public void addNewContact(String number) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent
                .putExtra(ContactsContract.Intents.Insert.PHONE, number);
        startActivityForResult(contactIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onResume() {
        readFromDb(getActivity());
        super.onResume();
    }
}
