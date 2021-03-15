package com.example.geeksproject.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.model.Chatlist;
import com.example.geeksproject.view.chats.ChatsActivity;
import com.example.geeksproject.view.dialog.DialogViewUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private List<Chatlist> list;
    private Context context;

    public ChatListAdapter(List<Chatlist> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chatlist,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final Chatlist chatlist = list.get(position);

        holder.tvName.setText(chatlist.getUserName());
        holder.tvDesc.setText(chatlist.getDescription());
        holder.tvDate.setText(chatlist.getDate());
        if (chatlist.getUrlProfile().equals("")){
            holder.profile.setImageResource(R.drawable.persin);
        }
        else if (chatlist.getUrlProfile().length()!=0 || chatlist.getUrlProfile()!=null){
            Glide.with(context).load(chatlist.getUrlProfile()).into(holder.profile);
        }



        if (chatlist.getDescription().length()==0 || chatlist.getDescription()==null){
            holder.tvDesc.setText("Busy");
        }
        else if (chatlist.getDescription().length()!=0 || chatlist.getUrlProfile()!=null){
            holder.tvDesc.setText(chatlist.getDescription());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID",chatlist.getUserID())
                        .putExtra("userName",chatlist.getUserName())
                        .putExtra("userProfile",chatlist.getUrlProfile()));
            }
        });
        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new DialogViewUser(context,chatlist);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDesc, tvDate;
        private CircularImageView profile;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
        }
    }
}


