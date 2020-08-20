package com.example.responsecounter.HomeActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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

import com.example.responsecounter.MiscellaneousActivites.ReportActivity;
import com.example.responsecounter.MiscellaneousActivites.settingsActivity;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.MiscellaneousActivites.NoteActivity;
import com.example.responsecounter.R;
import com.example.responsecounter.TestActivities.DuelButtonActivity;
import com.example.responsecounter.TestActivities.SingleButtonActivity;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.DataInterfaces.DataReceiveInterface;
import com.example.util.Interfaces.DataInterfaces.DataSaveInterface;
import com.example.util.Interfaces.DataInterfaces.ImageInterface;
import com.example.util.Models.PhysicianChoiceModel;
import com.example.util.Models.SubjectDetailModel;
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
  private Button testAvailable;
  private Button reportsAvailable;

  private final String TAG = "SubjectHome";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_subject_home);
    setPatientNavigation();

    image = findViewById(R.id.imgView_pic_patientHome);
    subjectInfo = findViewById(R.id.patientDetailsTextView);
    testAvailable = findViewById(R.id.patientHome_TestAvailable);
    reportsAvailable = findViewById(R.id.patientHome_ReportAvailable);
    reportsAvailable.setEnabled(false);
    testAvailable.setEnabled(false);
    String data = "Name:\t\t" + SubjectDetailModel.getInstance().getSubjectName() + "\n" +
        "Email:\t\t" + SubjectDetailModel.getInstance().getSubjectEmail() + "\n" +
        "Age:\t\t" + SubjectDetailModel.getInstance().getSubjectAge() + "\n";
    subjectInfo.setText(data);


  }

  @Override
  protected void onStart() {
    super.onStart();
    getPictureOnStart();
    new DatabaseConnector().getPhysicianControl(new DataReceiveInterface() {
      @Override
      public void status(boolean isSuucess) {
        if (isSuucess) {
          if(!EntityClass.getInstance().getPhysicianChoiceList().isEmpty()) {
            for(final PhysicianChoiceModel setting: EntityClass.getInstance().getPhysicianChoiceList()) {
              if(setting.isValue()) {
                if(setting.getLable() == EntityClass.getInstance().getLbl(SetupOptions.ReportLbl)) {
                  reportsAvailable.setEnabled(true);
                  reportsAvailable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      EntityClass.getInstance().changeValueAtPhysicianChoiseList(EntityClass.getInstance().getPhysicianChoiceList().indexOf(setting), false);
                      updatePhysicianChoice();
                      startActivity(new Intent(SubjectHome.this, ReportActivity.class));
                    }
                  });
                }
                if(setting.getLable() == EntityClass.getInstance().getLbl(SetupOptions.SingleButtonLbl)) {
                  testAvailable.setEnabled(true);
                  testAvailable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      EntityClass.getInstance().changeValueAtPhysicianChoiseList(EntityClass.getInstance().getPhysicianChoiceList().indexOf(setting), false);
                      updatePhysicianChoice();
                      startActivity(new Intent(SubjectHome.this, SingleButtonActivity.class));
                    }
                  });
                } else if(setting.getLable() == EntityClass.getInstance().getLbl(SetupOptions.DoubleButtonLbl)) {
                  testAvailable.setEnabled(true);
                  testAvailable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      EntityClass.getInstance().changeValueAtPhysicianChoiseList(EntityClass.getInstance().getPhysicianChoiceList().indexOf(setting), false);
                      updatePhysicianChoice();
                      startActivity(new Intent(SubjectHome.this, DuelButtonActivity.class));
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

  @Override
  protected void onRestart() {
    super.onRestart();
    reportsAvailable.setEnabled(false);
    testAvailable.setEnabled(false);
  }

  private void getPictureOnStart() {

    try {
      DatabaseConnector obj = new DatabaseConnector();
      obj.getSubjectImage(new ImageInterface() {
        @Override
        public void statusAndUri(boolean isSuccess, Uri uri) {
          if (isSuccess) {
            Picasso.get().load(uri).placeholder(R.drawable.background)
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
            startActivity(new Intent(SubjectHome.this, Help.class));
            break;
          }

          case (R.id.signOut): {
            dl.closeDrawers();
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
    new DatabaseConnector().updatePhysicianControl(new DataSaveInterface() {
      @Override
      public void successStatus(boolean isSuccess) {

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
    if (backButtonCount >= 1) {
      dl.closeDrawers();
      new DatabaseConnector().firebaseSignOut();
      startActivity(new Intent(SubjectHome.this, MainActivity.class));
    } else {
      Toast.makeText(this, "Press the back button once again to Sign Out.", Toast.LENGTH_SHORT).show();
      backButtonCount++;
    }
  }

}
