package com.example.util;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class CreateChannel extends Application {

  public static final String CHANNEL_1_ID = "channel1";

  @Override
  public void onCreate() {
    super.onCreate();
    createNotificationChannels();
  }

   private void createNotificationChannels() {
     if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
       NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Activity Available", NotificationManager.IMPORTANCE_DEFAULT);
       channel1.setDescription( "Response counter activity available");


       NotificationManager manager = getSystemService(NotificationManager.class);
       manager.createNotificationChannel(channel1) ;
     }
   }
}
