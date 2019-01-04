package com.example.monet_android1.contactdetailsdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.click.CallClickListner;
import com.example.monet_android1.contactdetailsdemo.fragment.CallDetailsFragment;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;

import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.contactList;
import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myCallsAppDatabase;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private Context context;
    private List<CallLog> callLogs;
    private String title = "";
    private CallClickListner callClickListner;
    private int i = 0;
    private CallLog callLog = new CallLog();
    private CallDetailsFragment callDetailsFragment = new CallDetailsFragment();

    public CallLogAdapter(Context context, List<CallLog> callLogs, CallClickListner callClickListner) {
        this.context = context;
        this.callLogs = callLogs;
        this.callClickListner = callClickListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_calls, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.id.setText(Integer.toString(callLogs.get(position).getId()));
        holder.date.setText(callLogs.get(position).getTime());
        holder.callType.setText(callLogs.get(position).getCallType());

        getName(callLogs.get(position).getMobile(), holder, position);

        if (callLogs.get(position).getCallType().equals("Incoming Call")
                || callLogs.get(position).getCallType().equals("Outgoing Call")) {
            holder.callType.setTextColor(Color.parseColor("#047b04"));
        } else {
            holder.callType.setTextColor(Color.parseColor("#cc0505"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = i + 1;
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i == 1) {
                            showPopup(callLogs.get(position).getMobile(), position, holder);
                        } else if (i >= 3) {
                            callLog.setId(callLogs.get(position).getId());
                            myCallsAppDatabase.myCallDao().deleteEntry(callLog);
                            callDetailsFragment.readFromDb(context);
                        }
                        i = 0;
                    }
                }, 500);
            }
        });
    }

    private String showPopup(String number, final int position, final ViewHolder holder) {
        String numberSearch = "";
        number = number.replace("+91", "");
        final String finalNumber = number;
        for (int i = 0; i < contactList.getMobile().size(); i++) {
            String searchNmber = contactList.getMobile().get(i);
            searchNmber = searchNmber.replace("+91", "");
            if (searchNmber.contains(number)) {
                numberSearch = number;
            }
        }

        if (number == numberSearch) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + finalNumber));
            context.startActivity(intent);
        } else {
            PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
            popupMenu.getMenu().add("Add Contact");
            popupMenu.getMenu().add("Call");

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getTitle().equals("Add Contact")) {
                        callClickListner.onItemClick(callLogs.get(position).getMobile());
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + finalNumber));
                        context.startActivity(intent);
                    }

                    return true;
                }
            });
            popupMenu.show();
        }
        return title;
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

    protected void getName(String number, ViewHolder holder, int position) {

        String name = "";
        number = number.replace("+91", "");
        for (int i = 0; i < contactList.getMobile().size(); i++) {
            String mobile = contactList.getMobile().get(i);
            if (mobile.contains(number)) {
                name = contactList.getName().get(i);
            }
        }

        if (name.isEmpty()) {
            holder.mobile.setText(callLogs.get(position).getMobile());
        } else {
            holder.mobile.setText(name);
            name = "";
        }

    }
}
