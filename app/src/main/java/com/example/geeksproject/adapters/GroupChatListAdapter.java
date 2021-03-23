package com.example.geeksproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.Notification.Data;
import com.example.geeksproject.R;
import com.example.geeksproject.model.ModelGroupChatList;
import com.example.geeksproject.view.GroupChat.GroupChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

//public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.HolderGroupChatList>{
//    private Context context;
//    private ArrayList<ModelGroupChat> groupChatList;
//
//    public GroupChatListAdapter(Context context, ArrayList<ModelGroupChat> groupChatList) {
//        this.context = context;
//        this.groupChatList = groupChatList;
//    }
//
//    @NonNull
//    @Override
//    public HolderGroupChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View view= LayoutInflater.from(context).inflate(R.layout.row_groupchats_list,parent,false);
//        return new HolderGroupChatList(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HolderGroupChatList holder, int position) {
//        ModelGroupChat model=groupChatList.get(position);
//        String groupId=model.getGroupId();
//        String groupIcon=model.getGroupIcon();
//        String groupTitle=model.getGroupTitle();
//        holder.groupTitleTv.setText(groupTitle);
//        Glide.with(context).load(groupIcon).into(holder.groupIconView);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    class HolderGroupChatList extends RecyclerView.ViewHolder{
//       private ImageView groupIconView;
//       private TextView groupTitleTv,nameTv,messageTv,timeTv;
//       public HolderGroupChatList(@Nullable View itemView) {
//           super(itemView);
//           groupIconView=itemView.findViewById(R.id.groupIconTv);
//           groupTitleTv=itemView.findViewById(R.id.grouptitleTv);
//           nameTv=itemView.findViewById(R.id.nameTv);
//           messageTv=itemView.findViewById(R.id.messageTv);
//           timeTv=itemView.findViewById(R.id.timeTv);
//       }
//   }
//
//}
public class GroupChatListAdapter extends RecyclerView.Adapter<GroupChatListAdapter.ViewHolder> {
    private List<ModelGroupChatList> coursesArrayList;
    private Context context;

    public GroupChatListAdapter(List<ModelGroupChatList> coursesArrayList, Context context) {
        this.coursesArrayList = coursesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new GroupChatListAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_groupchats_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatListAdapter.ViewHolder holder, int position) {

        ModelGroupChatList model = coursesArrayList.get(position);

        String groupId=model.getGroupId();
        String groupIcon=model.getGroupIcon();
        String groupTitle=model.getGroupTitle();



        holder.timeTv.setText("");
        holder.nameTv.setText("");
        holder.messageTv.setText("");


        loadLastMessage(model,holder);

        holder.groupTitleTv.setText(groupTitle);
        Glide.with(context).load(model.getGroupIcon()).into(holder.groupIconView);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);
            }
        });
//
    }

    private void loadLastMessage(ModelGroupChatList model, ViewHolder holder) {


        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        assert firebaseUser != null;


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(model.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String message = "" + ds.child("message").getValue();
                String timestamp = "" + ds.child("timeStamp").getValue();
                    String sender = "" + ds.child("sender").getValue();
                    String messageType=""+ds.child("type").getValue();


                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(timestamp));
                    String TImeDate = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                     if (messageType.equals("text")){
                         holder.messageTv.setText(message);
                     }
                     else if (messageType.equals("image")){
                         holder.messageTv.setVisibility(View.GONE);
                     }
                  //  holder.messageTv.setText(message);
                    holder.timeTv.setText(TImeDate);

                    Query reference1=firebaseFirestore.collection("Users").orderBy("userID").whereEqualTo("userID",sender);
                    reference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value==null){

                            }
                            else {
                                for (DocumentChange dc : value.getDocumentChanges()) {
//
                                    QueryDocumentSnapshot documentChange = dc.getDocument();
                                    String userName = documentChange.getString("userName");


                                    holder.nameTv.setText(userName);
                                }
                            }    }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return coursesArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView groupIconView;
        private TextView groupTitleTv,nameTv,messageTv,timeTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           groupIconView=itemView.findViewById(R.id.groupIconTv);
           groupTitleTv=itemView.findViewById(R.id.grouptitleTv);
           nameTv=itemView.findViewById(R.id.nameTv);
           messageTv=itemView.findViewById(R.id.messageTv);
           timeTv=itemView.findViewById(R.id.timeTv);

        }

    }
}
