package com.example.geeksproject.menu;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.geeksproject.Notification.Token;
import com.example.geeksproject.R;
import com.example.geeksproject.adapters.ChatListAdapter;
import com.example.geeksproject.databinding.FragmentChatsBinding;
import com.example.geeksproject.model.Chatlist;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private  FirebaseFirestore firebaseFirestore;
    private Handler handler = new Handler();

    private List<Chatlist> list;

    private FragmentChatsBinding binding;

    private ArrayList<String> allUserID;

    private ChatListAdapter adapter;


    private static final String TAG = "ChatsFragment";

    public ChatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chats, container, false);
        allUserID = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (firebaseUser!=null) {
            getChatList();
        }


        updateToken(FirebaseInstanceId.getInstance().getToken());
        return binding.getRoot();
    }

    private void getChatList() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        allUserID.clear();
        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                            String userID = snapshot.child("chatId").getValue(String.class);

                            binding.progressCircular.setVisibility(View.GONE);
                            allUserID.add(userID);
                    }
                             getUserInfo();
                 if (allUserID.size()==0){
                     binding.progressCircular.setVisibility(View.GONE);
                    // Toast.makeText(getContext(),"Please send text by clicking on floatinf button",Toast.LENGTH_LONG).show();
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void getUserInfo(){


        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : allUserID){
                    DocumentReference documentReference=firebaseFirestore.collection("Users").document(userID);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {
                                Chatlist chat = new Chatlist(
                                        documentSnapshot.getString("userID"),
                                        documentSnapshot.getString("userName"),
                                        "this is description..",
                                        "",
                                        documentSnapshot.getString("imageProfile")
                                );
                                list.add(chat);
                            }catch (Exception e){

                            }
                            if (adapter!=null){
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();

                            }


                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        adapter = new ChatListAdapter(list,getContext());
    }
public void updateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Token");
        Token token1=new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);

}

}


