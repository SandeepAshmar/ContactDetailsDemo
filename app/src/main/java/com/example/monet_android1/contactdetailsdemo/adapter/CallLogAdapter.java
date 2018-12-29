package com.example.monet_android1.contactdetailsdemo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.fragment.CallDetailsFragment;
import com.example.monet_android1.contactdetailsdemo.fragment.ContactsFragment;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;
import com.example.monet_android1.contactdetailsdemo.user.Contacts;

import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myContactAppDatabase;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private Context context;
    private List<CallLog> callLogs;
    private List<Contacts> contacts = myContactAppDatabase.myContactDao().getContactUsers();

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
                showPopup(callLogs.get(position).getMobile(), position, holder);
            }
        });
    }

    private void showPopup(String number, final int position, ViewHolder holder) {
        String numberSearch = "";
        number = number.replace("+91", "");
        final String finalNumber = number;
        for (int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getMobile().equals(number)){
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
                        saveNumber(position);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + finalNumber));
                        context.startActivity(intent);
                    }

                    return true;
                }
            });
            popupMenu.show();
        }
    }

    private void saveNumber(int position) {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.update_layout);
        final EditText name, mobile;
        Button update, cancel;
        TextView tv_update;
        name = dialog.findViewById(R.id.edt_updateName);
        mobile = dialog.findViewById(R.id.edt_updateMobile);
        update = dialog.findViewById(R.id.btn_update);
        cancel = dialog.findViewById(R.id.btn_updateCancel);
        tv_update = dialog.findViewById(R.id.tv_popUpdate);

        tv_update.setText("Save Number");
        update.setText("Save");

        name.setHint("Enter name");
        String number =callLogs.get(position).getMobile();
        number = number.replace("+91", "");
        mobile.setText(number);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
                }else if(mobile.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                }else if(mobile.getText().length() < 10){
                    Toast.makeText(context, "Please enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                }else{
                    Contacts contacts = new Contacts();
                    contacts.setName(name.getText().toString());
                    contacts.setMobile(mobile.getText().toString());
                    myContactAppDatabase.myContactDao().addContactUser(contacts);
                    Toast.makeText(context, "contact saved successfully", Toast.LENGTH_SHORT).show();
                    CallDetailsFragment.readFromDb(context);
                    ContactsFragment.readFromDb(context);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

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
        for (Contacts usr : contacts) {
            String userMobile = usr.getMobile();
            if (number.equals(userMobile)) {
                name = usr.getName();
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
