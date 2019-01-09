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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.activity.CallDetailsActivity;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import java.util.ArrayList;
import java.util.Random;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    private ContactList list;
    private ArrayList<String> colorList = new ArrayList<>();

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

        generateRandomNumber(holder);
        holder.name.setText(list.getName().get(position));
        String text = String.valueOf(holder.name.getText().charAt(0));
        holder.id.setText(text.toUpperCase());
        holder.mobile.setText(list.getMobile().get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(context, CallDetailsActivity.class);
                    intent.putExtra("mobile", list.getMobile().get(position));
                    intent.putExtra("name", list.getName().get(position));
                    intent.putExtra("color", colorList.get(position));
                    context.startActivity(intent);
                } else {
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

        TextView id;
        private TextView name;
        private TextView mobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tv_contactId);
            name = itemView.findViewById(R.id.tv_contactName);
            mobile = itemView.findViewById(R.id.tv_contactMobile);
        }
    }

    @SuppressLint("NewApi")
    private void generateRandomNumber(ViewHolder holder) {
        Random r = new Random();
        int red = r.nextInt(150 - 0 + 1) + 1;
        int green = r.nextInt(150 - 0 + 1) + 1;
        int blue = r.nextInt(150 - 0 + 1) + 1;

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.OVAL);
        draw.setColor(Color.rgb(red, green, blue));
        holder.id.setBackground(draw);
        String color =  String.format("#%02x%02x%02x", red, green, blue);
        color = color.replace("android.graphics.drawable.GradientDrawable@", "");
        colorList.add(color);
    }
}