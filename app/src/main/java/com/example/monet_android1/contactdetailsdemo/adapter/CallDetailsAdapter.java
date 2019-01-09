package com.example.monet_android1.contactdetailsdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.user.UserCallDetails;

public class CallDetailsAdapter extends RecyclerView.Adapter<CallDetailsAdapter.ViewHolder> {

    private Context context;
    private UserCallDetails callLogs;

    public CallDetailsAdapter(Context context, UserCallDetails callLogs) {
        this.context = context;
        this.callLogs = callLogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_call_details, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(callLogs.getMobile().contains("No data")){
            holder.noData.setVisibility(View.VISIBLE);
            holder.yesData.setVisibility(View.GONE);
        }else{
            holder.noData.setVisibility(View.GONE);
            holder.yesData.setVisibility(View.VISIBLE);
            holder.mobile.setText(callLogs.getMobile().get(position));
            holder.time.setText(callLogs.getTime().get(position));

            if(callLogs.getType().get(position).equals("Missed Call")){
                holder.type.setBackgroundResource(R.drawable.ic_missed_call_24dp);
            }else if(callLogs.getType().get(position).equals("Incoming Call")){
                holder.type.setBackgroundResource(R.drawable.ic_incoming_call_24dp);
            }else{
                holder.type.setBackgroundResource(R.drawable.ic_outgoing_call_24dp);
            }
        }
    }

    @Override
    public int getItemCount() {
        return callLogs.getMobile().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mobile, time, noData;
        ImageView type;
        LinearLayout yesData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mobile = itemView.findViewById(R.id.tv_mobileItemDetails);
            type = itemView.findViewById(R.id.img_callType);
            time = itemView.findViewById(R.id.tv_mobileItemTime);
            noData = itemView.findViewById(R.id.tv_noData);
            yesData = itemView.findViewById(R.id.ll_ifData);
        }
    }
}
