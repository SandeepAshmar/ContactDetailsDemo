package com.example.monet_android1.contactdetailsdemo.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import java.util.Random;

import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.callUser;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Viewholder> {

    private Context context;
    private ContactList list;

    public SearchAdapter(Context context, ContactList list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_contacts, viewGroup, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position) {
        generateRandomNumber(holder);
        holder.name.setText(list.getName().get(position));
        String text = String.valueOf(holder.name.getText().charAt(0));
        holder.id.setText(text.toUpperCase());
        holder.mobile.setText(list.getMobile().get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUser(list.getMobile().get(position), context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.getName().size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView id, name, mobile;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_contactId);
            name = itemView.findViewById(R.id.tv_contactName);
            mobile = itemView.findViewById(R.id.tv_contactMobile);
        }
    }

    @SuppressLint("NewApi")
    private void generateRandomNumber(Viewholder holder) {
        Random r = new Random();
        int red = r.nextInt(150 - 0 + 1) + 1;
        int green = r.nextInt(150 - 0 + 1) + 1;
        int blue = r.nextInt(150 - 0 + 1) + 1;

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        draw.setColor(Color.rgb(red, green, blue));
        holder.id.setBackground(draw);
    }
}
