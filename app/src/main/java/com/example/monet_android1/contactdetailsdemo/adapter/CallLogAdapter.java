package com.example.monet_android1.contactdetailsdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.activity.CallDetailsActivity;
import com.example.monet_android1.contactdetailsdemo.click.CallClickListner;
import com.example.monet_android1.contactdetailsdemo.user.CallDetails;

import java.util.ArrayList;
import java.util.Random;

import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.callUser;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.sendSMS;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private Context context;
    private CallDetails callDetails;
    private String title = "";
    private CallClickListner callClickListner;
    private ArrayList<String> colorList = new ArrayList<>();

    public CallLogAdapter(Context context, CallDetails callDetails, CallClickListner callClickListner) {
        this.context = context;
        this.callDetails = callDetails;
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

        generateRandomNumber(holder);
        if (callDetails.getName().get(position).isEmpty()) {
            holder.name.setText(callDetails.getMobile().get(position));
            holder.firstLetter.setText("Add");
        } else {
            holder.name.setText(callDetails.getName().get(position));
            String text = String.valueOf(holder.name.getText().charAt(0));
            holder.firstLetter.setText(text.toUpperCase());
        }

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(callDetails.getMobile().get(position),
                        callDetails.getName().get(position), position, holder);
            }
        });

        holder.firstLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUser(callDetails.getMobile().get(position), context);
            }
        });


    }

    private String showPopup(final String number, final String name, final int position, final ViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(context, holder.more);
        if (name.isEmpty()) {
            popupMenu.getMenu().add("Add to contact");
        } else {
            popupMenu.getMenu().add("Send message");
        }
        popupMenu.getMenu().add("Call Details");
        popupMenu.getMenu().add("Call");
        popupMenu.getMenu().add("Edit number before call");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().equals("Add to contact")) {
                    callClickListner.onItemClick(callDetails.getMobile().get(position));
                } else if (item.getTitle().equals("Call")) {
                    callUser(number, context);
                } else if (item.getTitle().equals("Send message")) {
                    sendSMS(number, context);
                } else if (item.getTitle().equals("Edit number before call")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, CallDetailsActivity.class);
                    intent.putExtra("mobile", number);
                    intent.putExtra("name", name);
                    intent.putExtra("color", colorList.get(position));
                    context.startActivity(intent);
                }

                return true;
            }
        });
        popupMenu.show();
        return title;
    }

    @Override
    public int getItemCount() {
        return callDetails.getMobile().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, firstLetter;
        ImageView more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_nameItem);
            firstLetter = itemView.findViewById(R.id.tv_firstLetter);
            more = itemView.findViewById(R.id.img_menu);

        }
    }

    @SuppressLint("NewApi")
    private void generateRandomNumber(ViewHolder holder) {
        Random r = new Random();
        int red = r.nextInt(150 - 0 + 1) + 1;
        int green = r.nextInt(150 - 0 + 1) + 1;
        int blue = r.nextInt(150 - 0 + 1) + 1;

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.RECTANGLE);
        draw.setColor(Color.rgb(red, green, blue));
        holder.firstLetter.setBackground(draw);
        String color = String.format("#%02x%02x%02x", red, green, blue);
        color = color.replace("android.graphics.drawable.GradientDrawable@", "");
        colorList.add(color);
    }
}
