package com.example.geeksproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.chats.ChatsActivity;
import com.example.geeksproject.model.Users;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private ArrayList<Users> coursesArrayList;
    private Context context;

    // creating constructor for our adapter class
    public ContactsAdapter(ArrayList<Users> coursesArrayList, Context context) {
        this.coursesArrayList = coursesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Users courses = coursesArrayList.get(position);
        holder.courseNameTV.setText(courses.getUserName());
      // holder.courseDurationTV.setText(courses.getBio());

        Glide.with(context).load(courses.getImageProfile()).into(holder.courseDurationTV);
        holder.courseDescTV.setText(courses.getBio());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID", courses.getUserID())
                .putExtra("userName",courses.getUserName())
                .putExtra("userProfile",courses.getImageProfile()));

            }

        });


    }


    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return coursesArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView courseNameTV;
        private final ImageView courseDurationTV;
        private final TextView courseDescTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            courseNameTV = itemView.findViewById(R.id.tv_username);
            courseDurationTV = itemView.findViewById(R.id.image_profile);
            courseDescTV = itemView.findViewById(R.id.tv_desc);
        }
    }
}
