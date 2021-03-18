package com.example.geeksproject.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.model.Chatlist;
import com.example.geeksproject.model.chat.Chats;
import com.example.geeksproject.view.chats.ChatsActivity;
import com.example.geeksproject.view.dialog.DialogViewUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private List<Chatlist> list;
    private Context context;
    String thelastMessage;
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

        initMessage(chatlist.getUserID(),holder.tvDesc);

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
    private void initMessage(String userid,final TextView lastChat){
        thelastMessage="default";
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats=snapshot.getValue(Chats.class);
                    assert firebaseUser != null;
                    assert chats != null;
                    if (chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(userid)
                            || chats.getReceiver().equals(userid) && chats.getSender().equals(firebaseUser.getUid())){
                        thelastMessage=chats.getTextMessage();

                    }
                }
                switch (thelastMessage){
                    case "default":lastChat.setText("No message");
                        break;
                    default: lastChat.setText(thelastMessage);
                    break;
                }
                thelastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}


