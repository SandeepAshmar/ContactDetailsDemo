package com.example.monet_android1.contactdetailsdemo.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
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
import com.example.monet_android1.contactdetailsdemo.user.CallLog;

import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myCallsAppDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallDetailsFragment extends Fragment {

    private static RecyclerView rv_calls;
    private LinearLayoutManager layoutManager;
    private static CallLogAdapter adapter;
    private static TextView tv_noCalls;
    private BroadcastReceiver br;

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
        layoutManager = new LinearLayoutManager(getActivity());
        rv_calls.setLayoutManager(layoutManager);
        readFromDb(getActivity());

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromDb(getActivity());
            }
        };
        return view;
    }

    public void readFromDb(Context context) {
        try{
            List<CallLog> callLogs = myCallsAppDatabase.myCallDao().getCallDetials();
            adapter = new CallLogAdapter(context, callLogs, callClickListner);
            rv_calls.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if(callLogs.size()>0){
                rv_calls.setVisibility(View.VISIBLE);
                tv_noCalls.setVisibility(View.GONE);
            }else{
                rv_calls.setVisibility(View.GONE);
                tv_noCalls.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void addNewContact(String number) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent
                .putExtra(ContactsContract.Intents.Insert.PHONE, number);
        startActivityForResult(contactIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Contact added successfully", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Contact not added", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
