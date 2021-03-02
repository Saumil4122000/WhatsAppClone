package com.example.geeksproject.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.geeksproject.R;
import com.example.geeksproject.adapters.CallListAdapter;
import com.example.geeksproject.model.CallsList;

import java.util.ArrayList;
import java.util.List;

public class CallsFragment extends Fragment {
    List<CallsList> list=new ArrayList<>();
    private RecyclerView recyclerView;
    public CallsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getcallList();
    }
    private void getcallList()
    {
//        list.add(new CallsList("1",
//                "Shraddha Kapoor",
//                "https://www.tutorialspoint.com/images/tp-logo-diamond.png",
//                "missed",
//                "15-02-2021,09:30PM"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_calls, container, false);
        //recyclerView=view.findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setAdapter(new CallListAdapter(list,getContext()));
        return view;
    }

}