package com.example.responsecounter.TestActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.PhysicianChoiceModel;
import com.example.util.Models.PosturalStabilityDataModel;
import com.example.util.SetupOptions;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PosturalStabilityActivity extends AppCompatActivity implements SensorEventListener {
  private static final String TAG = "PosturalStabilityActivity";
  private Button startButton;
  private TextView timerText;
  private int counter = 40;
  private boolean testStarted = false;
  private SensorManager sensorManager;
  Sensor accelerometer, gyroScope, accelerometer_uncalibrated, accelerometer_linear;

  private ArrayList<Float> accelerometer_xValue = new ArrayList<>();
  private ArrayList<Float> accelerometer_yValue = new ArrayList<>();
  private ArrayList<Float> accelerometer_zValue = new ArrayList<>();

  private ArrayList<Float> accelerometer_xValue_uncalibrated = new ArrayList<>();
  private ArrayList<Float> accelerometer_yValue_uncalibrated = new ArrayList<>();
  private ArrayList<Float> accelerometer_zValue_uncalibrated = new ArrayList<>();

  private ArrayList<Float> accelerometer_xValue_linear = new ArrayList<>();
  private ArrayList<Float> accelerometer_yValue_linear = new ArrayList<>();
  private ArrayList<Float> accelerometer_zValue_linear = new ArrayList<>();

  private ArrayList<Float> gyroscope_xValue = new ArrayList<>();
  private ArrayList<Float> gyroscope_yValue = new ArrayList<>();
  private ArrayList<Float> gyroscope_zValue = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_postural_stability);

    startButton = findViewById(R.id.posturalStability_startsBtn);
    timerText = findViewById(R.id.posturalStability_timerTextView);

    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    accelerometer_uncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
    accelerometer_linear = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    gyroScope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startButton.setEnabled(false);
        testStarted = true;
        hideNavigationBar();
        new CountDownTimer(counter*1000, 1000) {
          @Override
          public void onTick(long millisUntilFinished) {
            timerText.setText(String.valueOf(counter));
            counter--;
          }
          @Override
          public void onFinish() {
            startButton.setEnabled(true);
            testStarted = false;
            counter = 40;
            timerText.setText("Finished");
            sensorManager.unregisterListener(PosturalStabilityActivity.this);
            sensorManager.flush(PosturalStabilityActivity.this);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
              @Override
              public void run() {
                PosturalStabilityDataModel record = new PosturalStabilityDataModel(
                    accelerometer_xValue,
                    accelerometer_yValue,
                    accelerometer_zValue,
                    accelerometer_xValue_uncalibrated,
                    accelerometer_yValue_uncalibrated,
                    accelerometer_zValue_uncalibrated,
                    accelerometer_xValue_linear,
                    accelerometer_yValue_linear,
                    accelerometer_zValue_linear,
                    gyroscope_xValue,
                    gyroscope_yValue,
                    gyroscope_zValue
                );
                String timestamp = String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli());
                new DatabaseConnector().saveData("TestData/PosturalStabilityData/" + timestamp + "/", record, new MyStatListener() {
                  @Override
                  public void status(boolean isSuccess, Object obj) {
                    if(isSuccess) {
                      Log.d(TAG, "status: " + "Postural Stability Data Saved!");
                      updatePhysicianChoice();
                      finish();
                    }
                  }

                  @Override
                  public void onFailure(String errMessage) {
                    Log.d(TAG, "onFailure: " + errMessage);
                  }
                });
                Log.d(TAG, "Accelerometer Readings:\n" +
                    "X: " + accelerometer_xValue +
                    "y: " + accelerometer_yValue +
                    "z: " + accelerometer_zValue);

                Log.d(TAG, "Gyroscope Readings:\n" +
                    "X: " + gyroscope_xValue +
                    "y: " + gyroscope_yValue +
                    "z: " + gyroscope_zValue);

              }
            }, 1000);
          }
        }.start();
        Log.d(TAG, "onCreate: Sensor Services Started" );
        sensorManager.registerListener(PosturalStabilityActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(PosturalStabilityActivity.this, gyroScope, SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(PosturalStabilityActivity.this, accelerometer_linear, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(PosturalStabilityActivity.this, accelerometer_uncalibrated, SensorManager.SENSOR_DELAY_UI);
        Log.d(TAG, "onCreate: Registered Sensor Accelerometer and Gyroscope!!") ;
      }
    });
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  @Override
  protected void onStart() {
    super.onStart();

    for (PhysicianChoiceModel setting : EntityClass.getInstance().getPhysicianChoiceList()) {
      if (setting.getLable().equals(EntityClass.getInstance().getLbl(SetupOptions.posturalStabilityLbl))) {
        EntityClass.getInstance().changeValueAtPhysicianChoiseList(EntityClass.getInstance().getPhysicianChoiceList().indexOf(setting), false);
        break;
      }
    }
  }

  /**
   * This is used to update the realtime database to inform that the test has been taken.
   */
  private void updatePhysicianChoice() {
    new DatabaseConnector().updatePhysicianControl(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {
        if(isSuccess) Log.d(TAG, "status: Updated Choice");

      }

      @Override
      public void onFailure(String errMessage) {
        Toast.makeText(PosturalStabilityActivity.this, errMessage, Toast.LENGTH_LONG).show();
      }
    });
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onSensorChanged(SensorEvent event) {
    if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      accelerometer_xValue.add(event.values[0]);
      accelerometer_yValue.add(event.values[1]);
      accelerometer_zValue.add(event.values[2]);
    } else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER_UNCALIBRATED) {
      accelerometer_xValue_uncalibrated.add(event.values[0]);
      accelerometer_yValue_uncalibrated.add(event.values[1]);
      accelerometer_zValue_uncalibrated.add(event.values[2]);
    } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
      accelerometer_xValue_linear.add(event.values[0]);
      accelerometer_yValue_linear.add(event.values[1]);
      accelerometer_zValue_linear.add(event.values[2]);
    } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
      gyroscope_xValue.add(event.values[0]);
      gyroscope_yValue.add(event.values[1]);
      gyroscope_zValue.add(event.values[2]);
    }
//    Log.d(TAG, "onSensorChanged: "+ event.sensor.getName() + "X:" + event.values[0] + " Y:" + event.values[1] + " Z:" +event.values[2]);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if(testStarted) {
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private void hideNavigationBar() {
    final View decorView = this.getWindow().getDecorView();
    final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        PosturalStabilityActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            decorView.setSystemUiVisibility(uiOptions);
          }
        });
      }
    };
    timer.scheduleAtFixedRate(task, 1, 2);
  }
}