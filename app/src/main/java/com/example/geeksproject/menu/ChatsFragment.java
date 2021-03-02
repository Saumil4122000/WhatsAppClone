package com.example.geeksproject.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geeksproject.R;
import com.example.geeksproject.adapters.ChatListAdapter;
import com.example.geeksproject.model.ChatList;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    List<ChatList> list=new ArrayList<>();
    private RecyclerView recyclerView;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getchatList();
    }
    private void getchatList()
    {
        list.add(new ChatList("1","Shraddha Kapoor","hello Shraddha","15-02-2021","https://www.tutorialspoint.com/images/tp-logo-diamond.png"));
        // list.add(new ChatList("2","Ananya Pandey","hello Ananya","14-02-2021","https://www.bing.com/images/search?view=detailV2&ccid=XY5zlK6Z&id=54B6FC6D8EB074F7A1AAED49648CD97CC555DD89&thid=OIP.XY5zlK6ZtXUmE54sUdNOIwHaEw&mediaurl=https%3a%2f%2fth.bing.com%2fth%2fid%2fR5d8e7394ae99b57526139e2c51d34e23%3frik%3did1VxXzZjGRJ7Q%26riu%3dhttp%253a%252f%252fwww.beautifulpix.com%252fwp-content%252fuploads%252f2019%252f02%252fAnanya-Pandey-Hot-and-Sexy-29.jpg%26ehk%3d2xoANNNrgDxf%252fPEUmDIh75SZDeluAvkFK3V73jwhJfM%253d%26risl%3d%26pid%3dImgRaw&exph=720&expw=1121&q=ananya+pande&simid=608031575821910316&ck=FE97120FED301288CBDBB03F20CE49E1&selectedIndex=2&FORM=IRPRST&ajaxhist=0.png"));
        list.add(new ChatList("3","Alia Bhatt","hello Alia","13-02-2021","https://moodle.htwchur.ch/pluginfile.php/124614/mod_page/content/4/example.jpg"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chats, container, false);
        //recyclerView=view.findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setAdapter(new ChatListAdapter(list,getContext()));
        return view;
    }


}