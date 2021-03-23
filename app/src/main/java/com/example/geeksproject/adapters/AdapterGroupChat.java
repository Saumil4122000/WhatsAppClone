package com.example.geeksproject.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.model.ModelGroupChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderModelGroupChat>{
    private static final int MSG_TYPE_RIGHT=0;
    private static final int MSG_TYPE_LEFT=1;
    private final Context context;
    private final ArrayList<ModelGroupChat> modelGroupChatsList;
    private final FirebaseAuth firebaseAuth;
    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChats) {
        this.context = context;
        this.modelGroupChatsList = modelGroupChats;
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderModelGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT){
            View view=LayoutInflater.from(context).inflate(R.layout.row_groupchat_right,parent,false);
            return new HolderModelGroupChat(view);
        }
        else{
            View view=LayoutInflater.from(context).inflate(R.layout.row_groupchat_left,parent,false);
            return new HolderModelGroupChat(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (modelGroupChatsList.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderModelGroupChat holder, int position) {
        ModelGroupChat modelGroupChat=modelGroupChatsList.get(position);
        String message=modelGroupChat.getMessage();
        String timestamp=modelGroupChat.getTimestamp();
        String senderUid=modelGroupChat.getSender();
        String messageType=modelGroupChat.getType();
//Log.d("TATATATAT"+"HI",timestamp);
        Log.d("TATATATAT",message);

        Calendar cal= Calendar.getInstance(Locale.ENGLISH);
      //  cal.setTimeInMillis(Long.parseLong(timestamp));
        String TImeDate= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

        if (messageType.equals("text")){
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setText(message);

        }
        else {
            holder.messageTv.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);
            Glide.with(context).load(message).into(holder.messageIv);
        }



        holder.timeTv.setText(TImeDate);
        setUserName(modelGroupChat,holder,senderUid);
    }

    private void setUserName(ModelGroupChat modelGroupChat, HolderModelGroupChat holder, String senderUid) {
        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        assert firebaseUser != null;

        Query reference1=firebaseFirestore.collection("Users").orderBy("userID").whereEqualTo("userID",senderUid);
        reference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value==null){

                }
                else {
                    for (DocumentChange dc : value.getDocumentChanges()) {

                        QueryDocumentSnapshot documentChange = dc.getDocument();
                        String userName = documentChange.getString("userName");

                       // Log.d("ANMANAMNAMNM", userName);
                        holder.nameTv.setText(userName);
                    }
                }    }
        });

    }



    @Override
    public int getItemCount() {
        return modelGroupChatsList.size();
    }


    static class HolderModelGroupChat extends RecyclerView.ViewHolder{
    private final TextView nameTv;
        private final TextView messageTv;
        private final TextView timeTv;
    private final ImageView messageIv;
        public HolderModelGroupChat(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.nameTv);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);
            messageIv=itemView.findViewById(R.id.messageIv);
        }
    }
}
