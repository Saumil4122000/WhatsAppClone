package com.example.geeksproject.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.geeksproject.R;
import com.example.geeksproject.model.CallsList;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.Holder>{
    private List<CallsList> list;
    private Context context;


    public CallListAdapter(List<CallsList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_call_list,parent,false);
        return new Holder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final CallsList calllist = list.get(position);

        holder.tvName.setText(calllist.getUserName());

        holder.tvDate.setText(calllist.getDate());
        if (calllist.getCalltype().equals("missed")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_downward_24));
                holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));
            }
        } else if (calllist.getCalltype().equals("income")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_upward_24));
                holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_upward_24));
                holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
            }

        }
        if (calllist.getUrlProfile().equals("")) {
          holder.profile.setImageResource(R.drawable.persin);
        } else {
            Glide.with(context).load(calllist.getUrlProfile()).into(holder.profile);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class Holder extends RecyclerView.ViewHolder {
        private TextView tvDate,tvName;
        private CircularImageView profile;
        private ImageView arrow;
        public Holder(@NonNull View itemView) {
            super(itemView);
            arrow=itemView.findViewById(R.id.img_arrow);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
        }
    }
}
