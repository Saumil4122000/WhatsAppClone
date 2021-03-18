package com.example.geeksproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.model.user.Users;
import com.example.geeksproject.view.chats.ChatsActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Users> coursesArrayList;
    private Context context;

    public ContactsAdapter(List<Users> coursesArrayList, Context context) {
        this.coursesArrayList = coursesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {

        Users courses = coursesArrayList.get(position);

        holder.courseNameTV.setText(courses.getUserName());
if (courses.getImageProfile().isEmpty() || courses.getImageProfile()==null){
    Glide.with(context).load(R.drawable.persin).into(holder.courseDurationTV);
}
else if(!courses.getImageProfile().isEmpty() || courses.getImageProfile()!=null)
    {
        Glide.with(context).load(courses.getImageProfile()).into(holder.courseDurationTV);
}

        else if(courses.getBio().length()==0 || courses.getBio()==null){
            holder.courseDescTV.setText("Busy");
        }
        else
        {
            holder.courseDescTV.setText(courses.getBio());
        }


        holder.courseDescTV.setText(courses.getBio());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID", courses.getUserID())
                        .putExtra("userName", courses.getUserName())
                        .putExtra("userProfile", courses.getImageProfile())
                        .putExtra("userPhone",courses.getUserPhone())
                        .putExtra("bio",courses.getBio())
                        .putExtra("typingTo",courses.getTypingTo())
                );

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
        private Button btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTV = itemView.findViewById(R.id.tv_username);
            courseDurationTV = itemView.findViewById(R.id.image_profile);
            courseDescTV = itemView.findViewById(R.id.tv_desc);

        }

    }
}
