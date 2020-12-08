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
import com.google.android.material.math.MathUtils;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class DualButtonActivity extends AppCompatActivity {

  private DrawerLayout dl;
  private ActionBarDrawerToggle toggle;

  private CardView layoutCard;
  private CountDownTimer timer = null;
  private EditText timeBox;
  private Button btn1, btn2, startBtn;
  private TextView txtView;
  private final String TAG = "DualButtonActivity";
  ArrayList<Long> t1 = new ArrayList<Long>();
  ArrayList<Long> t2 = new ArrayList<Long>();
  ArrayList<Double> timeList = new ArrayList<Double>();

  ArrayList<Float> x1 = new ArrayList<Float>();
  ArrayList<Float> y1 = new ArrayList<Float>();

  ArrayList<Float> x2 = new ArrayList<Float>();
  ArrayList<Float> y2 = new ArrayList<Float>();

  /**
   * These variables are used to hold the number of times the buttons are pressed respectively.(count1 and count2)
   * count3 is used to track wrong taps.
   */
  private int count1 = 0, count2 = 0, count3 = 0;

  /**
   * This variable is used to count the number of times the test is been taken.
   */
  private int testNum = 1;

  /**
   * These arrays hold the counts of number of correct taps and the wrong taps.
   */
  private int[] correctTaps = new int[3], wrongTaps = new int[3];
  private static DecimalFormat df = new DecimalFormat("0.000000");

  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dual_button);

    setNavigation();

    layoutCard = findViewById(R.id.doubleButton_test_click_event);
    startBtn = findViewById(R.id.startBtn);
    startBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startBtn.setEnabled(false);
        txtView = findViewById(R.id.resultTb);
        txtView.setText("Total Count: 0");
        count1 = 0;
        count2 = 0;
        count3 = 0;
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
        break;
      }
    }
  }

//  @Override
//  public boolean onTouchEvent(MotionEvent event) {
//
//    if(!startBtn.isEnabled()) {
//    }
//    return super.onTouchEvent(event);
//  }

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

              float center2X = (float) btn2.getWidth()/2;
              float center2Y = (float) btn2.getHeight()/2;

              if (Math.pow(touchX - centerX, 2) + Math.pow(touchY - centerY, 2) < Math.pow(centerX, 2)) {
                count1++;
              } else {
                count3++;
                x1.add(touchX);
                y1.add(touchY);
              }
              t1.add(ZonedDateTime.now().toInstant().toEpochMilli());
            }
            return false;
          }
        });

        btn2.setOnTouchListener(new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
              float touchX = (float) event.getX();
              float touchY  = (float) event.getY();

              float centerX = (float) v.getWidth()/2;
              float centerY = (float) v.getHeight()/2;

              if (Math.pow(touchX - centerX, 2) + Math.pow(touchY - centerY, 2) < Math.pow(centerX, 2)) {
                count2++;
                x2.add(touchX);
                y2.add(touchY);
              } else {
                count3++;
              }
              t2.add(ZonedDateTime.now().toInstant().toEpochMilli());
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

        if(x1.size() > x2.size()) {
          for(int i = 0; i < x2.size(); i++) {
            double xDist = Math.pow(Math.abs(x1.get(i) - x2.get(i)) / dm.xdpi, 2);
            double yDist = Math.pow(Math.abs(y1.get(i) - y2.get(i)) / dm.ydpi, 2);
            dist.add(Math.sqrt(xDist + yDist));
          }
        } else {
          for(int i = 0; i < x1.size(); i++) {
            double xDist = Math.pow(Math.abs(x1.get(i) - x2.get(i)) / dm.xdpi, 2);
            double yDist = Math.pow(Math.abs(y1.get(i) - y2.get(i)) / dm.ydpi, 2);
            dist.add(Math.sqrt(xDist + yDist));
          }
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
          txtView.setText("Total Count: " + (count1 + count2));
          return;
        }

        startBtn.setEnabled(false);
        timeBox.setText("Finished !!");
        txtView = findViewById(R.id.resultTb);
        correctTaps[testNum - 1] = count1 + count2;
        wrongTaps[testNum - 1] = count3;
        txtView.setText("Total Count: " + correctTaps[testNum - 1]);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          public void run() {
            int size = 0;
            if(t1.size() > t2.size()) size = t2.size();
            else size = t1.size();
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
            sum = 0;
            for(double val : timeList) {
              sum = sum + Math.pow(Math.abs(val -avgTime), 2);
            }

            stdDev = Math.sqrt((double) sum/timeList.size());

            final TapModel record = new TapModel();

            record.setAvgSpeed(avgDistance()/avgTime);
            record.setSdTimebwTaps(stdDev);
            record.setAvgTimeBetweenTaps(avgTime);
            record.setCorrectTapsCount((correctTaps[0] + correctTaps[1] + correctTaps[2]) / 3);
            record.setWrongTapsCount((wrongTaps[0] + wrongTaps[1] + wrongTaps[2]) / 3);
            try {
              String timestamp = String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli());
              DatabaseConnector dbObj = new DatabaseConnector();
              dbObj.saveData("TestData/DoubleTapData/" + timestamp + "/", record, new MyStatListener() {
                @Override
                public void status(boolean isSuccess, Object obj) {
                  if (isSuccess) {
                    updatePhysicianChoice();
                    startBtn.setEnabled(true);
                    startActivity(new Intent(DualButtonActivity.this, SubjectHome.class));
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
