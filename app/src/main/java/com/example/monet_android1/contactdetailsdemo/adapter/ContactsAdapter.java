package com.example.monet_android1.contactdetailsdemo.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    private PopupMenu popup;
    private ContactList list;
    private int i = 0;

    public ContactsAdapter(Context context, ContactList list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_contacts, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsAdapter.ViewHolder holder, final int position) {
        i = position+1;
        holder.id.setText(String.valueOf(i));
        holder.name.setText(list.getName().get(position));
        holder.mobile.setText(list.getMobile().get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + list.getMobile().get(position)));
                    context.startActivity(callIntent);
                }else{
                    Toast.makeText(context, "You don't assign permission.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.getName().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView id, name, mobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tv_contactId);
            name = itemView.findViewById(R.id.tv_contactName);
            mobile = itemView.findViewById(R.id.tv_contactMobile);
        }
    }
}