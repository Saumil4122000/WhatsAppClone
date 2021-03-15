package com.example.geeksproject.tools;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioService {
    private Context context;
    private MediaPlayer tmpMediaPlater;
    private OnplayCallBack onplayCallBack;
    public AudioService(Context context) {
        this.context = context;
        //this.onplayCallBack = new MediaPlayer();
    }

    public void playAudioFromURL(String url, OnplayCallBack onPlayCallBack){
        this.onplayCallBack=onPlayCallBack;
        if (tmpMediaPlater!=null){
            tmpMediaPlater.stop();
        }
        MediaPlayer mediaPlayer=new MediaPlayer();
        try{

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            tmpMediaPlater=mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                onPlayCallBack.onFinished();
            }
        });
    }
   public interface OnplayCallBack{
        void onFinished();
   }
}
