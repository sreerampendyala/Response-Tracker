package com.example.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.R;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.PhysicianChoiceModel;

public class CreateChannel extends Service {

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

  @Override
  public IBinder onBind (Intent arg0) {
    return null;
  }

  public int onStartCommand (Intent intent , int flags , int startId) {
    super .onStartCommand(intent , flags , startId) ;
    startService(new Intent(this, DatabaseConnector.class));
    listenDb();
    return START_STICKY ;
  }

  private void  listenDb() {
    new DatabaseConnector().getPhysicianControl(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {
        Log.d("TAG", "status: STATUS STATUS");
        if(isSuccess && !EntityClass.getInstance().getPhysicianChoiceList().isEmpty()) {
          for(PhysicianChoiceModel setting: EntityClass.getInstance().getPhysicianChoiceList()) {
            if(setting.isValue()) {
              if(setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.ReportLbl))) {
                addNotification(setting.getLable(), SetupOptions.ReportLbl.ordinal());
              } else if(setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.SingleButtonLbl))) {
                addNotification(setting.getLable(), SetupOptions.SingleButtonLbl.ordinal());
              } else if(setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.DoubleButtonLbl))) {
                addNotification(setting.getLable(), SetupOptions.DoubleButtonLbl.ordinal());
              }
            }
          }
        }
      }

      @Override
      public void onFailure(String errMessage) {

      }
    });
  }

  private void addNotification(String activityName, int code) {
    Log.d("class", "addNotification: $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    if(EntityClass.getInstance().isSubject()) {
      Intent finalIntent = new Intent(this, SubjectHome.class);
      finalIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, finalIntent, 0);


      NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(getApplicationContext(), CreateChannel.CHANNEL_1_ID)
          .setSmallIcon(R.drawable.ic_stat_name)
          .setContentTitle("Activity Available")
          .setContentText(activityName)
          .setContentIntent(resultPendingIntent)
          .setAutoCancel(true);

      NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
      notificationManager.notify(code, mbuilder.build());
      startForeground(2, mbuilder.build());
    }
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    Intent intent = new Intent("com.android.ServiceStopped");
    Log.d("TAG", "onTaskRemoved: STOPPED STOPPED");
    sendBroadcast(intent);

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d("TAG", "onDestroy: DESTROY DESTROY");
    stopService(new Intent(this, DatabaseConnector.class));
  }
}
