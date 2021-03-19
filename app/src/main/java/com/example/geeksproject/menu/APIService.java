package com.example.geeksproject.menu;

import com.example.geeksproject.Notification.Myresponse;
import com.example.geeksproject.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
   @Headers({
           "Content-Type:application/json",
           "Authorization:key=AAAAkAPrK1M:APA91bFSM0dLM2xJX4FkQY_yAIbNiTqh2pz85gpcSyLNbxh-phqqMok9-_QgP_4hrYkgPAy0beeAXdq75gAIYx-UCMLTQEsLOTEXnFkeRqUmxcX7OmZYozPSMuuMFYzXu26JZlw8674j"
   })

   @POST("fcm/send")
   Call<Myresponse> sendNotification(@Body Sender body);

}
