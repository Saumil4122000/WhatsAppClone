package com.example.geeksproject.menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geeksproject.R;
import com.example.geeksproject.adapters.GroupChatListAdapter;
import com.example.geeksproject.model.ModelGroupChatList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupChatsFragment extends Fragment {


private RecyclerView groupRv;
FirebaseAuth firebaseAuth;
private ArrayList<ModelGroupChatList> groupChatList;
private GroupChatListAdapter adapterGroupList;
    public GroupChatsFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_group,container,false);
       groupRv=view.findViewById(R.id.groupsRv);
       firebaseAuth=FirebaseAuth.getInstance();
       loadGrouplist();
       return view;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.main,menu);
////        menu.findItem(R.id.action_more).setVisible(false);
//        MenuItem item=menu.findItem(R.id.menu_search);
//        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (!TextUtils.isEmpty(query)){
//                    searchGroupChatList(query);
//                }
//                else{
//                    loadGrouplist();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (!TextUtils.isEmpty(newText)){
//                    searchGroupChatList(newText);
//                }
//                else{
//                    loadGrouplist();
//                }
//                return false;
//            }
//        });
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    private void searchGroupChatList(final String query) {
//        groupChatList=new ArrayList<>();
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                groupChatList.clear();
//                for (DataSnapshot ds:dataSnapshot.getChildren()){
//
//                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists()) {
//                        if (ds.child("groupTitle").toString().toLowerCase().contains(query)) {
//                            ModelGroupChat model = ds.getValue(ModelGroupChat.class);
//                            groupChatList.add(model);
//                        }
//                    }
//                }
//     adapterGroupList=new GroupChatListAdapter(getActivity(),groupChatList);
//                groupRv.setAdapter(adapterGroupList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id=item.getItemId();
//        if (id==R.id.action_new_group){
//            startActivity(new Intent(getActivity(), GroupCreateActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void loadGrouplist() {
        groupChatList=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                  //  Log.d("NEWGROUP", String.valueOf(ds.child("Participants").child(firebaseAuth.getUid())));
                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists()) {

                        ModelGroupChatList model = ds.getValue(ModelGroupChatList.class);
                        groupChatList.add(model);
                    }
                }
                adapterGroupList=new GroupChatListAdapter(groupChatList,getContext());
                groupRv.setAdapter(adapterGroupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}