package com.example.monet_android1.contactdetailsdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;
import com.example.monet_android1.contactdetailsdemo.user.Contacts;

import java.util.ArrayList;
import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myContactAppDatabase;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private Context context;
    private List<CallLog> callLogs;

    public CallLogAdapter(Context context, List<CallLog> callLogs) {
        this.context = context;
        this.callLogs = callLogs;
    }

    @NonNull
    @Override
    public CallLogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_calls, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.id.setText(Integer.toString(callLogs.get(position).getId()));
        holder.date.setText(callLogs.get(position).getTime());
        holder.callType.setText(callLogs.get(position).getCallType());

        getName(callLogs.get(position).getMobile(), holder, position);

        if(callLogs.get(position).getCallType().equals("Incoming Call")
                || callLogs.get(position).getCallType().equals("Outgoing Call")){
            holder.callType.setTextColor(Color.parseColor("#047b04"));
        }else{
            holder.callType.setTextColor(Color.parseColor("#cc0505"));
        }
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mobile, date, id, callType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tv_callsNumberId);
            mobile = itemView.findViewById(R.id.tv_callsMobileNumber);
            date = itemView.findViewById(R.id.tv_callsDate);
            callType = itemView.findViewById(R.id.tv_callType);

        }
    }

    protected void getName(String number, ViewHolder holder, int position){
        List<Contacts> contacts = myContactAppDatabase.myContactDao().getContactUsers();
        String name = "";
        number = number.replace("+91", "");
        for (Contacts usr : contacts) {
            String userMobile = usr.getMobile();
            if (number.equals(userMobile)) {
                name = usr.getName();
            }
        }

        if(name.isEmpty()){
            holder.mobile.setText(callLogs.get(position).getMobile());
        }else{
            holder.mobile.setText(name);
            name = "";
        }

    }
}
