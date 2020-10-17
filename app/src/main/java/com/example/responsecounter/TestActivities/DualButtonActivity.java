package com.example.responsecounter.TestActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.MiscellaneousActivites.HelpActivity;
import com.example.responsecounter.MiscellaneousActivites.NoteActivity;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.PhysicianChoiceModel;
import com.example.util.Models.TapModel;
import com.example.util.SetupOptions;
import com.google.android.material.navigation.NavigationView;

public class DualButtonActivity extends AppCompatActivity {

  private DrawerLayout dl;
  private ActionBarDrawerToggle toggle;

  private CountDownTimer timer = null;
  private EditText timeBox;
  private Button btn1, btn2, startBtn;
  private TextView txtView;
  private final String TAG = "DualButtonActivity";

  /**
   * These variables are used to hold the number of times the buttons are pressed respectively.
   */
  private int count1 = 0, count2 = 0;

  /**
   * This variable is used to count the number of times the test is been taken.
   */
  private int testNum = 1;

  /**
   * These arrays hold the counts of number of correct taps and the wrong taps.
   */
  private int[] correctTaps = new int[3], wrongTaps = new int[3];

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dual_button);

    setNavigation();

    startBtn = findViewById(R.id.startBtn);
    startBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startBtn.setEnabled(false);
        txtView = findViewById(R.id.resultTb);
        txtView.setText("Total Count: 0");
        count1 = 0;
        count2 = 0;
        Timer();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    for (PhysicianChoiceModel setting : EntityClass.getInstance().getPhysicianChoiceList()) {
      if (setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.DoubleButtonLbl))) {
        EntityClass.getInstance().changeValueAtPhysicianChoiseList(EntityClass.getInstance().getPhysicianChoiceList().indexOf(setting), false);
        updatePhysicianChoice();
        break;
      }
    }
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  public void Timer() {
    timeBox = (EditText) findViewById(R.id.timerBox);
    btn1 = findViewById(R.id.buttonCount1);
    btn2 = findViewById(R.id.buttonCount2);
    startBtn = findViewById(R.id.startBtn);

    timer = new CountDownTimer(30000, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        timeBox.setText("Remaining: " + millisUntilFinished / 1000);

        btn1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            count1++;
          }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            count2++;
          }
        });
      }

      @Override
      public void onFinish() {
        if (EntityClass.getInstance().isPractice()) return;

        startBtn.setEnabled(true);
        timeBox.setText("Finished !!");
        txtView = findViewById(R.id.resultTb);
        correctTaps[testNum-1] = count1 + count2;
        txtView.setText("Total Count: " + correctTaps[testNum-1]);

        if (testNum < 3) {
          testNum++;
          createDialogue();
          return;
        }

        final TapModel record = new TapModel();
        record.setCount((correctTaps[0] + correctTaps[1] + correctTaps[2])/3);

        try {
          String timestamp = String.valueOf(System.currentTimeMillis());
          DatabaseConnector dbObj = new DatabaseConnector();
          dbObj.saveData("TestData/DoubleTapData/" + timestamp + "/", record, new MyStatListener() {
            @Override
            public void status(boolean isSuccess, Object obj) {
              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                public void run() {
                  finish();
                }
              }, 5000);   //5 seconds
            }

            @Override
            public void onFailure(String errMessage) {
                Log.d(TAG, "onFailure: error saving the data" + errMessage);
            }
          });
        } catch (Exception e) {
          Log.d(TAG, "onFinish: " + e.getClass().toString() + " " + e.getMessage());
        }

      }
    };
    timer.start();
  }

  /**
   * This Method is used to create a dialogue after every test.
   */
  private void createDialogue() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // Get the layout inflater
    builder.setMessage(R.string.ready_for_next_round)
        // Add action buttons
        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
            // sign in the user ...

          }
        });
    builder.create().show();
  }

  /**
   * This method is used to set the functionality of the items in the nav menu(hamburger sign).
   */
  private void setNavigation() {

    dl = (DrawerLayout) findViewById(R.id.dl_duel_Button_Activity);
    toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
    toggle.setDrawerIndicatorEnabled(true);
    dl.addDrawerListener(toggle);
    toggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView nav_View = (NavigationView) findViewById(R.id.nav_View_DuelButton);
    nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
          case (R.id.note): {
            dl.closeDrawers();
            startActivity(new Intent(DualButtonActivity.this, NoteActivity.class));
            break;
          }

          case (R.id.myhelp): {
            dl.closeDrawers();
            startActivity(new Intent(DualButtonActivity.this, HelpActivity.class));
            break;
          }

          case (R.id.signOut): {
            dl.closeDrawers();
            new DatabaseConnector().firebaseSignOut();
            startActivity(new Intent(DualButtonActivity.this, MainActivity.class));
            break;
          }

        }
        return true;
      }
    });

  }

  /**
   * This is used to update the realtime database to inform that the test has been taken.
   */
  private void updatePhysicianChoice() {
    new DatabaseConnector().updatePhysicianControl(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {

      }

      @Override
      public void onFailure(String errMessage) {
        Toast.makeText(DualButtonActivity.this, errMessage, Toast.LENGTH_LONG).show();
      }
    });
  }


  @Override
  public void onBackPressed() {
    dl.closeDrawers();
    startActivity(new Intent(DualButtonActivity.this, SubjectHome.class));
  }
}
