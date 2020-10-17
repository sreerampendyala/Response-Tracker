package com.example.responsecounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.PhysicianHome;
import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.SignInActivities.SignInActivity;
import com.example.util.EntityClass;
import com.example.util.Models.PhysicianDetailModel;
import com.example.util.Models.SubjectDetailModel;
import com.example.util.SaveSharedPreference;


public class MainActivity extends AppCompatActivity {

  /**
   * This button is pressed if the user chooses patient in the screen.
   */
  private Button isPatient;

  /**
   * This button is pressed if the user chooses physician in the screen.
   */
  private Button isPhysician;

  private int backButtonCount = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    checkIfAlreadyLoggedIn();

    isPatient = findViewById(R.id.button_isPatient);
    isPhysician = findViewById(R.id.button_isPhysician);

    isPatient.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EntityClass.getInstance().setSubject(true);
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
      }
    });


    isPhysician.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EntityClass.getInstance().setSubject(false);
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
      }
    });
  }

  /**
   * Check if the user is already logged in and if logged in start from home page.
   */
  private void checkIfAlreadyLoggedIn() {
    if (SaveSharedPreference.getUserName(MainActivity.this).length() != 0) {
      if (SaveSharedPreference.isSubjectLogin(MainActivity.this)) {
        SubjectDetailModel.getInstance().setSubjectEmail(SaveSharedPreference.getUserName(MainActivity.this));
        SubjectDetailModel.getInstance().setSubjectName(SaveSharedPreference.getName(MainActivity.this));
        SubjectDetailModel.getInstance().setSubjectAge(SaveSharedPreference.getAge(MainActivity.this));
        SubjectDetailModel.getInstance().setUserIdInDb(SaveSharedPreference.getUserId(MainActivity.this));
        PhysicianDetailModel.getInstance().setPhysicianEmail(SaveSharedPreference.getPhysicainEmailForSubject(MainActivity.this));

        //
        Intent intent = new Intent(MainActivity.this, SubjectHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
      } else {
        PhysicianDetailModel.getInstance().setPhysicianEmail(SaveSharedPreference.getUserName(MainActivity.this));
        PhysicianDetailModel.getInstance().setPhysicianName(SaveSharedPreference.getName(MainActivity.this));
        PhysicianDetailModel.getInstance().setUserIdInDb(SaveSharedPreference.getUserId(MainActivity.this));
        //
        Intent intent = new Intent(MainActivity.this, PhysicianHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
      }
    }
  }


  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onRestart() {
    super.onRestart();
  }

  @Override
  public void onBackPressed() {
    if (backButtonCount >= 1) {
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
