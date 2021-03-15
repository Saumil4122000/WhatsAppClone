package com.example.geeksproject.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geeksproject.R;
import com.example.geeksproject.model.Chatlist;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

        List<Chatlist> list=new ArrayList<>();
        private RecyclerView recyclerView;
        public ChatsFragment() {
            // Required empty public constructor
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getchatList();
        }
        private void getchatList()
        {
            list.add(new Chatlist("1","Shraddha Kapoor","hello Shraddha","15-02-2021","https://www.tutorialspoint.com/images/tp-logo-diamond.png"));
                 list.add(new Chatlist("2","Ananya Pandey","hello Ananya","14-02-2021","https://www.bing.com/images/search?view=detailV2&ccid=XY5zlK6Z&id=54B6FC6D8EB074F7A1AAED49648CD97CC555DD89&thid=OIP.XY5zlK6ZtXUmE54sUdNOIwHaEw&mediaurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR5d8e7394ae99b57526139e2c51d34e23%3frik%3did1VxXzZjGRJ7Q%26riu%3dhttp%253a%252f%252fwww.beautifulpix.com%252fwp-content%252fuploads%252f2019%252f02%252fAnanya-Pandey-Hot-and-Sexy-29.jpg%26ehk%3d2xoANNNrgDxf%252fPEUmDIh75SZDeluAvkFK3V73jwhJfM%253d%26risl%3d%26pid%3dImgRaw&exph=720&expw=1121&q=ananya+pande&simid=608031575821910316&ck=FE97120FED301288CBDBB03F20CE49E1&selectedIndex=2&FORM=IRPRST&ajaxhist=0.png"));
            list.add(new Chatlist("3","Alia Bhatt","hello Alia","13-02-2021","https://moodle.htwchur.ch/pluginfile.php/124614/mod_page/content/4/example.jpg"));

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view= inflater.inflate(R.layout.fragment_chats, container, false);

            return view;
        }

//        private static final String TAG = "ChatsFragment";
//
//        public ChatsFragment() {
//            // Required empty public constructor
//        }
//
//        private FirebaseUser firebaseUser;
//        private DatabaseReference reference;
//        private FirebaseFirestore firestore;
//        private Handler handler = new Handler();
//
//        private List<Chatlist> list;
//
//        private FragmentChatsBinding binding;
//
//        private ArrayList<String> allUserID;
//
//        private ChatListAdapter adapter;
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            // Inflate the layout for this fragment
//            binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chats, container, false);
//
//            list = new ArrayList<>();
//            allUserID = new ArrayList<>();
//
//            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            adapter = new ChatListAdapter(list,getContext());
//            binding.recyclerView.setAdapter(adapter);
//
//            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//            reference = FirebaseDatabase.getInstance().getReference();
//            firestore = FirebaseFirestore.getInstance();
//
//
//            if (firebaseUser!=null) {
//                getChatList();
//            }
//
//            return binding.getRoot();
//        }
//
//
//        private void getChatList() {
//
//            binding.progressCircular.setVisibility(View.VISIBLE);
//            list.clear();
//            allUserID.clear();;
//            reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        String userID = Objects.requireNonNull(snapshot.child("chatid").getValue()).toString();
//                        Log.d(TAG, "onDataChange: userid "+userID);
//
//                        binding.progressCircular.setVisibility(View.GONE);
//                        allUserID.add(userID);
//                    }
//                    getUserInfo();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//
//            });
//        }
//
//        private void getUserInfo(){
//
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    for (String userID : allUserID){
//                        firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                Log.d(TAG, "onSuccess: ddd"+documentSnapshot.getString("userName"));
//                                try {
//                                    Chatlist chat = new Chatlist(
//                                            documentSnapshot.getString("userID"),
//                                            documentSnapshot.getString("userName"),
//                                            "this is description..",
//                                            "",
//                                            documentSnapshot.getString("imageProfile")
//                                    );
//                                    list.add(chat);
//                                }catch (Exception e){
//                                    Log.d(TAG, "onSuccess: "+e.getMessage());
//                                }
//                                if (adapter!=null){
//                                    adapter.notifyItemInserted(0);
//                                    adapter.notifyDataSetChanged();
//
//                                    Log.d(TAG, "onSuccess: adapter "+adapter.getItemCount());
//                                }
//                            }
//
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(TAG, "onFailure: Error L"+e.getMessage());
//                            }
//                        });
//                    }
//                }
//            });
//        }

    }

