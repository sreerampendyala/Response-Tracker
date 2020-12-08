package com.example.responsecounter.TestActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.google.rpc.Help;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class SingleButtonActivity extends AppCompatActivity {

  private DrawerLayout dl;
  private ActionBarDrawerToggle toggle;

  private CardView layoutCard;
  private CountDownTimer timer = null;
  private EditText timeBox;
  private Button btn1, startBtn;
  private TextView txtView;
  private final String TAG = "SignleButtonActivity";
  ArrayList<Long> t1 = new ArrayList<Long>();
  ArrayList<Long> t2 = new ArrayList<Long>();
  ArrayList<Double> timeList = new ArrayList<Double>();

  ArrayList<Float> x1 = new ArrayList<Float>();
  ArrayList<Float> y1 = new ArrayList<Float>();

  /**
   * These variables are used to hold the number of times the buttons are pressed respectively.(count1 and count2)
   * count3 is used to track wrong taps.
   */
  private int count1 = 0, count3 = 0;

  private int count2 = 0;

  /**
   * This variable is used to count the number of times the test is been taken.
   */
  private int testNum = 1;

  /**
   * These arrays hold the counts of number of correct taps and the wrong taps.
   */
  private int[] correctTaps = new int[3], wrongTaps = new int[3];
  private static DecimalFormat df = new DecimalFormat("0.000000");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_single_button);

    setNavigation();

    layoutCard = findViewById(R.id.singleButton_test_click_event);
    startBtn = findViewById(R.id.single_startBtn);
    timeBox = (EditText) findViewById(R.id.single_Btn_timerBox);
    btn1 = findViewById(R.id.single_buttonCount1);
    startBtn = findViewById(R.id.single_startBtn);

    startBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startBtn.setEnabled(false);
        txtView = findViewById(R.id.single_resultTb);
        txtView.setText("Total Count: 0");
        count1 = 0;
        count3 = 0;
        Timer();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();

    for (PhysicianChoiceModel setting : EntityClass.getInstance().getPhysicianChoiceList()) {
      if (setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.SingleButtonLbl))) {
        EntityClass.getInstance().changeValueAtPhysicianChoiseList(EntityClass.getInstance().getPhysicianChoiceList().indexOf(setting), false);
        break;
      }
    }
  }

  public void Timer() {

    timer = new CountDownTimer(30000, 1000) {
      @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
      @Override
      public void onTick(long millisUntilFinished) {
        timeBox.setText("Remaining: " + millisUntilFinished / 1000);

        layoutCard.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            count3++;
          }
        });



        btn1.setOnTouchListener(new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
              float touchX = (float) event.getX();
              float touchY  = (float) event.getY();
              float centerX = (float) v.getWidth()/2;
              float centerY = (float) v.getHeight()/2;

              if (Math.pow(touchX - centerX, 2) + Math.pow(touchY - centerY, 2) < Math.pow(centerX, 2)) {
                count1++;
                if(count2%2 == 0) {
                  t1.add(ZonedDateTime.now().toInstant().toEpochMilli());
                } else {
                  t2.add(ZonedDateTime.now().toInstant().toEpochMilli());
                }
                count2++;
              } else {
                count3++;
                x1.add(touchX);
                y1.add(touchY);
              }
            }
            return false;
          }
        });
      }

      private double avgDistance() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double distance = 0.000;
        ArrayList<Double> dist  =  new ArrayList<Double>();

        for(int i = 0; i < x1.size(); i++) {
          double xDist = Math.pow(Math.abs(x1.get(i)) / dm.xdpi, 2);
          double yDist = Math.pow(Math.abs(y1.get(i)) / dm.ydpi, 2);
          dist.add(Math.sqrt(xDist + yDist));
        }

        if(dist.size() !=0) {
          double sum = 0;
          for (double val : dist) {
            sum = sum + val;
          }
          distance = sum / dist.size() ;
        }

        return  distance * 2.54;
      }

      @SuppressLint("SetTextI18n")
      @Override
      public void onFinish() {
        if (EntityClass.getInstance().isPractice()) {
          txtView.setText("Total Count: " + (count1));
          return;
        }

        startBtn.setEnabled(false);
        timeBox.setText("Finished !!");

        correctTaps[testNum - 1] = count1;
        wrongTaps[testNum - 1] = count3;
        txtView.setText("Total Count: " + correctTaps[testNum - 1]);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          public void run() {

            int size = 0;
            size = Math.min(t1.size(), t2.size());

            for (int i = 0; i < size; i ++) {
              timeList.add(((double)Math.abs(t1.get(i) - t2.get(i)))/1000);
            }
            if (testNum < 3) {
              testNum++;
              t1.clear();
              t2.clear();
              createDialogue();
              return;
            } else if (testNum == 4) startBtn.setEnabled(false);
            Log.d(TAG, "&^&^&^&^&^&^&^" + timeList);

            double avgTime, stdDev;
            double sum = 0;
            for (double val : timeList) {
              sum = sum + val;
            }

            avgTime = sum/timeList.size();
            avgTime = Double.parseDouble(df.format(avgTime));
            Log.d(TAG, "&^&^&^&^&^&^&^  avgTime" + avgTime);


            double sd_sum = 0;
            for(double val : timeList) {
              sd_sum = sd_sum + Math.pow(Math.abs(val -avgTime), 2);
            }

            stdDev = Math.sqrt((double) sd_sum/timeList.size());

            Log.d(TAG, "&^&^&^&^&^&^&^  stdDev" + stdDev);

            final TapModel record = new TapModel();

            record.setAvgSpeed(avgDistance()/avgTime);
            record.setSdTimebwTaps(stdDev);
            record.setAvgTimeBetweenTaps(avgTime);
            record.setCorrectTapsCount((correctTaps[0] + correctTaps[1] + correctTaps[2]) / 3);
            record.setWrongTapsCount((wrongTaps[0] + wrongTaps[1] + wrongTaps[2]) / 3);
            try {
              String timestamp = String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli());
              DatabaseConnector dbObj = new DatabaseConnector();
              dbObj.saveData("TestData/SingleTapData/" + timestamp + "/", record, new MyStatListener() {
                @Override
                public void status(boolean isSuccess, Object obj) {
                  if (isSuccess) {
                    updatePhysicianChoice();
                    startBtn.setEnabled(true);
                    startActivity(new Intent(SingleButtonActivity.this, SubjectHome.class));
                    finish();
                  }
                }

                @Override
                public void onFailure(String errMessage) {
                  Log.d(TAG, "onFailure: error saving the data" + errMessage);
                  startBtn.setEnabled(true);
                }
              });
            } catch (Exception e) {
              Log.d(TAG, "onFinish: " + e.getClass().toString() + " " + e.getMessage());
              startBtn.setEnabled(true);
            }
          }
        }, 1000);
      }
    };
    timer.start();
  }

  private void setNavigation() {
    dl = (DrawerLayout) findViewById(R.id.dl_SingleButton_Activity);
    toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
    toggle.setDrawerIndicatorEnabled(true);
    dl.addDrawerListener(toggle);
    toggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView nav_View = (NavigationView) findViewById(R.id.nav_View_Single);
    nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
          case (R.id.note): {
            dl.closeDrawers();
            startActivity(new Intent(SingleButtonActivity.this, NoteActivity.class));
            break;
          }

          case (R.id.myhelp): {
            dl.closeDrawers();
            startActivity(new Intent(SingleButtonActivity.this, HelpActivity.class));
            break;
          }

          case (R.id.signOut): {
            dl.closeDrawers();
            new DatabaseConnector().firebaseSignOut();
            startActivity(new Intent(SingleButtonActivity.this, MainActivity.class));
            break;
          }
        }
        return true;
      }
    });

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
            startBtn.setEnabled(true);
          }
        });
    builder.create().show();
  }

  private void updatePhysicianChoice() {
    new DatabaseConnector().updatePhysicianControl(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {

      }

      @Override
      public void onFailure(String errMessage) {
        Toast.makeText(SingleButtonActivity.this, errMessage, Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  public void onBackPressed() {
    dl.closeDrawers();
    startActivity(new Intent(SingleButtonActivity.this, SubjectHome.class));
  }
}
