package com.example.responsecounter.HomeActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.responsecounter.Instructions.DualButtonInstructions;
import com.example.responsecounter.Instructions.SingleButtonInstructions;
import com.example.responsecounter.MiscellaneousActivites.HelpActivity;
import com.example.responsecounter.MiscellaneousActivites.ReportActivity;
import com.example.responsecounter.MiscellaneousActivites.settingsActivity;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.MiscellaneousActivites.NoteActivity;
import com.example.responsecounter.R;
import com.example.responsecounter.TestActivities.DualButtonActivity;
import com.example.responsecounter.TestActivities.SingleButtonActivity;
import com.example.util.CreateChannel;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;

import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.PhysicianChoiceModel;
import com.example.util.Models.SubjectDetailModel;
import com.example.util.SaveSharedPreference;
import com.example.util.SetupOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.rpc.Help;
import com.squareup.picasso.Picasso;


public class SubjectHome extends AppCompatActivity {

  private DrawerLayout dl;
  private ActionBarDrawerToggle toggle;
  private TextView subjectInfo;
  private int backButtonCount = 0;
  private ImageView image;
  private Button singleTestBtn, doubleTestBtn;
  private Button reportsAvailable;

  private final String TAG = "SubjectHome";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_subject_home);
    setPatientNavigation();

    image = findViewById(R.id.imgView_pic_patientHome);
    subjectInfo = findViewById(R.id.patientDetailsTextView);
    singleTestBtn =findViewById(R.id.subject_singletest_button);
    doubleTestBtn = findViewById(R.id.subject_duelTest_button);
    reportsAvailable = findViewById(R.id.patientHome_ReportAvailable);
    reportsAvailable.setEnabled(false);
    singleTestBtn.setEnabled(false);
    doubleTestBtn.setEnabled(false);
    String data = "Name:\t\t" + SubjectDetailModel.getInstance().getSubjectName() + "\n" +
        "Email:\t\t" + SubjectDetailModel.getInstance().getSubjectEmail() + "\n" +
        "Age:\t\t" + SubjectDetailModel.getInstance().getSubjectAge() + "\n";
    subjectInfo.setText(data);
    buttonAction();
  }

  public void buttonAction() {
    new DatabaseConnector().getPhysicianControl(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {
        if (isSuccess) {
          if(!EntityClass.getInstance().getPhysicianChoiceList().isEmpty()) {
            for(final PhysicianChoiceModel setting: EntityClass.getInstance().getPhysicianChoiceList()) {
              if(setting.isValue()) {

                if(setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.ReportLbl))) {
                  reportsAvailable.setEnabled(true);
                  reportsAvailable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      reportsAvailable.setEnabled(false);
                      try{
                        removeNotification(SetupOptions.ReportLbl.ordinal());
                      } catch (Exception e) {
                        Log.d(TAG, "onClick: " + e.getMessage());
                      }
                      EntityClass.getInstance().changeValueAtPhysicianChoiseList(EntityClass.getInstance().getPhysicianChoiceList().indexOf(setting), false);
                      updatePhysicianChoice();
                      startActivity(new Intent(SubjectHome.this, ReportActivity.class));
                    }
                  });
                } else if(setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.SingleButtonLbl))) {
                  singleTestBtn.setEnabled(true);
                  singleTestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      singleTestBtn.setEnabled(false);
                      try{
                        removeNotification(SetupOptions.SingleButtonLbl.ordinal());
                      } catch (Exception e) {
                        Log.d(TAG, "onClick: " + e.getMessage());
                      }
                      startActivity(new Intent(SubjectHome.this, SingleButtonInstructions.class));
                    }
                  });
                } else if(setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.DoubleButtonLbl))) {
                  doubleTestBtn.setEnabled(true);
                  doubleTestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      doubleTestBtn.setEnabled(false);
                      try{
                        removeNotification(SetupOptions.DoubleButtonLbl.ordinal());
                      } catch (Exception e) {
                        Log.d(TAG, "onClick: " + e.getMessage());
                      }
                      startActivity(new Intent(SubjectHome.this, DualButtonInstructions.class));
                    }
                  });
                }
              }
            }
          }
        }
      }

      @Override
      public void onFailure(String errMessage) {
        //
      }
    });
  }

  private void removeNotification(int code) {
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(code);

  }
  @Override
  protected void onStart() {
    super.onStart();
    getPictureOnStart();
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    stopMyService();
    notificationManager.cancelAll();
  }

  @Override
  protected void onStop() {
    super.onStop();
    startMyService();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy: %GGGGGGGG%");
  }


  private void startMyService() {
    startService(new Intent(this, CreateChannel.class));
  }

  private void stopMyService() {
    stopService(new Intent(this, CreateChannel.class));
  }

  @Override
  protected void onRestart() {
    super.onRestart();
  }

  private void getPictureOnStart() {

    try {
      DatabaseConnector obj = new DatabaseConnector();
      obj.getSubjectImage(new MyStatListener() {
        @Override
        public void status(boolean isSuccess, Object obj) {
          Uri uri = Uri.parse(String.valueOf(obj));
          if (isSuccess) {
            Picasso.get().load(uri).placeholder(R.drawable.user_picture_24dp)
                .fit()
                .into(image);
          }
        }

        @Override
        public void onFailure(String errMessage) {
          //
        }

      });

    } catch (Exception ex) {
      Log.d(TAG, "getPictureOnStart: " + ex.getMessage());
    }

  }

  private void setPatientNavigation() {
    dl = (DrawerLayout) findViewById(R.id.dl_Subject_Home_Activity);
    toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
    toggle.setDrawerIndicatorEnabled(true);
    dl.addDrawerListener(toggle);
    toggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView nav_View = (NavigationView) findViewById(R.id.nav_Subject_View_Home);
    nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
          case (R.id.note): {
            dl.closeDrawers();
            startActivity(new Intent(SubjectHome.this, NoteActivity.class));
            break;
          }

          case (R.id.settings): {
            dl.closeDrawers();
            startActivity(new Intent(SubjectHome.this, settingsActivity.class));
            break;
          }

          case (R.id.myhelp): {
            dl.closeDrawers();
            startActivity(new Intent(SubjectHome.this, HelpActivity.class));
            break;
          }

          case (R.id.signOut): {
            dl.closeDrawers();
            SaveSharedPreference.clearUserData(SubjectHome.this);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            startMyService();
            notificationManager.cancelAll();
            new DatabaseConnector().firebaseSignOut();
            startActivity(new Intent(SubjectHome.this, MainActivity.class));
            break;
          }

        }
        return true;
      }
    });
  }

  private void updatePhysicianChoice() {
    new DatabaseConnector().updatePhysicianControl(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {

      }

      @Override
      public void onFailure(String errMessage) {
        Toast.makeText(SubjectHome.this, errMessage, Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if(backButtonCount >= 1)  {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_HOME);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);

    } else {
      Toast.makeText(this, "Press the back button once again to exit the app", Toast.LENGTH_SHORT).show();
      backButtonCount++;
    }
  }

}
