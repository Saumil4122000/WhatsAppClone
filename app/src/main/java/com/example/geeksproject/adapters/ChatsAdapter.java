package com.example.geeksproject.adapters;

import android.animation.TimeAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.InterMediateActivity;
import com.example.geeksproject.R;
import com.example.geeksproject.model.chat.Chats;
import com.example.geeksproject.tools.AudioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter {
    private AudioService audioService;
    private ImageButton tmpBtnplay;
    private List<Chats> list;
    private Context context;
    private String profile_send;
    private String profile_receive;
    private ImageView imageChat;
    private ImageButton btnPlay;
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
        private FirebaseUser firebaseUser;
        private OnItemClickListner mListner;
    public ChatsAdapter(List<Chats> list, Context context,String profile_send,String profile_receive) {
        this.list = list;
        this.context = context;
        this.profile_receive=profile_receive;
        this.profile_send=profile_send;
        this.audioService=new AudioService(context);
    }
    public interface OnItemClickListner{
        void onItemClick(int position);
    }
    public void setOnItemClickListner(OnItemClickListner listner){
        mListner=listner;
    }
    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ReceivedMessageHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new SentMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        Chats message = list.get(position);
        switch (holder.getItemViewType()) {
            case MSG_TYPE_RIGHT:
                ((SentMessageHolder) holder).bind(message);
//
                break;
            case MSG_TYPE_LEFT:
                ((ReceivedMessageHolder) holder).bind(message);
        }


    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder{
        private TextView textMessage;
        private CircularImageView c;
        private LinearLayout layoutText,layoutImage,layoutVoice;
        private ImageView imageChat;
        private ContactsAdapter.ViewHolder tmpHolder;
       // private ImageView imageSeen;
        private TextView time;
        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            context=itemView.getContext();
            layoutVoice=itemView.findViewById(R.id.layout_voice);
            c=itemView.findViewById(R.id.profile_image_new);
            imageChat=itemView.findViewById(R.id.image_chat);
            textMessage=itemView.findViewById(R.id.tv_text_message);
            layoutImage=itemView.findViewById(R.id.layout_image);
            layoutText=itemView.findViewById(R.id.layout_text);
            btnPlay=itemView.findViewById(R.id.btn_play_chat);
           // imageSeen=itemView.findViewById(R.id.chat_tick);
            time=itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListner!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            mListner.onItemClick(position);
                        }
                    }
                }
            });

        }

        void bind(Chats messageModel) {

            Glide.with(context).load(profile_send).into(c);
            switch (messageModel.getType()) {
                case "TEXT":
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);
                    String time_date=messageModel.getDateTime();
                    String[] res=time_date.split("[,]",0);
                    time.setText(res[1]);
                    textMessage.setText(messageModel.getTextMessage());
                    break;
                case "IMAGE":
                    Glide.with(context).load(messageModel.getUrl()).into(imageChat);
                    layoutText.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);

                    break;
                case "VOICE":
                    layoutVoice.setVisibility(View.VISIBLE);
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onClick(View v) {
                            if (tmpBtnplay != null) {
                                tmpBtnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));

                            }
                            btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                            audioService.playAudioFromURL(messageModel.getUrl(), new AudioService.OnplayCallBack() {
                                @Override
                                public void onFinished() {
                                    btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));

                                }
                            });
                            tmpBtnplay = btnPlay;
                        }
                    });
                    break;
            }

        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        private TextView textMessage,time;
        private CircularImageView c;
        private LinearLayout layoutText,layoutImage,layoutVoice;
        private ImageView imageChat;
        private ContactsAdapter.ViewHolder tmpHolder;
        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            layoutVoice=itemView.findViewById(R.id.layout_voice);
            c=itemView.findViewById(R.id.profile_image_new);
            imageChat=itemView.findViewById(R.id.image_chat);
            textMessage=itemView.findViewById(R.id.tv_text_message);
            layoutImage=itemView.findViewById(R.id.layout_image);
            layoutText=itemView.findViewById(R.id.layout_text);
            btnPlay=itemView.findViewById(R.id.btn_play_chat);
            time=itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListner!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            mListner.onItemClick(position);
                        }
                    }
                }
            });

        }

        void bind(Chats messageModel) {

            Glide.with(context).load(profile_receive).into(c);
            switch (messageModel.getType()) {
                case "TEXT":
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);
                    String time_date=messageModel.getDateTime();
                    String[] res=time_date.split("[,]",0);
                    time.setText(res[1]);
                    textMessage.setText(messageModel.getTextMessage());
                break;
                case "IMAGE":
                    Glide.with(context).load(messageModel.getUrl()).into(imageChat);
                    layoutText.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    break;
                case "VOICE":
                    layoutVoice.setVisibility(View.VISIBLE);
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onClick(View v) {
                            if (tmpBtnplay!=null){
                                tmpBtnplay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));

                            }
                            btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                            audioService.playAudioFromURL(messageModel.getUrl(), new AudioService.OnplayCallBack() {
                                @Override
                                public void onFinished() {
                                    btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));

                                }
                            });
                            tmpBtnplay=btnPlay;
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
