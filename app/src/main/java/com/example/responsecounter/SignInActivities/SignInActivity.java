package com.example.responsecounter.SignInActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.PhysicianHome;
import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.MiscellaneousActivites.PasswordReset;
import com.example.responsecounter.R;
import com.example.responsecounter.SignUpActivities.PhysicianSignUpActivity;
import com.example.responsecounter.SignUpActivities.SubjectSignUpActivity;
import com.example.util.CreateChannel;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.SaveSharedPreference;

public class SignInActivity extends AppCompatActivity {
  /**
   * Sigin button and forgot password button
   */
  private Button signInbtn, forgotpassword;
  /**
   * signUp button
   */
  private TextView signUp;
  /**
   * Email text box
   */
  private AutoCompleteTextView email;
  /**
   * Password text box
   */
  private TextView pwd;
  private ProgressBar pgr;
  private final String TAG = "SignInActivity";



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    signInbtn = findViewById(R.id.signUpBtn);
    signUp = findViewById(R.id.signIn_tv);
    email = findViewById(R.id.username_tb);
    pwd = findViewById(R.id.pwd_tb);
    pgr = findViewById(R.id.signIn_Progress);
    forgotpassword = findViewById(R.id.forgot_password_button);

    forgotpassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(SignInActivity.this, PasswordReset.class));
      }
    });

    signInbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if(!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(pwd.getText().toString())) {
          pgr.setVisibility(View.VISIBLE);
          signInbtn.setEnabled(false);
          checkCreds(email.getText().toString(), pwd.getText().toString());
        } else {
          Toast.makeText(SignInActivity.this, "Feilds Should not be empty", Toast.LENGTH_LONG).show();
          Log.d("MainActivity", "Creds error");
        }

      }
    });

    signUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(EntityClass.getInstance().isSubject()) {
          startActivity(new Intent(SignInActivity.this, SubjectSignUpActivity.class));
        } else startActivity(new Intent(SignInActivity.this, PhysicianSignUpActivity.class));
      }
    });
  }

  /**
   * Validate the login details.
   * @param email email of the user
   * @param password password of the user.
   */
  private void checkCreds(String email, String password) {

    try {
      Log.d("MainActivity", "checkCreds:  after constructor");
      DatabaseConnector obj = new DatabaseConnector();
      obj.validateLogin(email, password, new MyStatListener() {
        @Override
        public void status(boolean isSuccess, Object obj) {
          if(isSuccess) {
            pgr.setVisibility(View.INVISIBLE);
            signInbtn.setEnabled(true);
            SaveSharedPreference.setUserNameAndType(SignInActivity.this);
            if(EntityClass.getInstance().isSubject()) {
              EntityClass.getInstance().startMyService(getApplicationContext());
              startActivity(new Intent(SignInActivity.this, SubjectHome.class));
            }
            else startActivity(new Intent(SignInActivity.this, PhysicianHome.class));
          }
        }

        @Override
        public void onFailure(String errMessage) {
          pgr.setVisibility(View.INVISIBLE);
          signInbtn.setEnabled(true);
          Log.d(TAG, "onFailure: Failed validating credentials from the database" + errMessage);
          Toast.makeText(SignInActivity.this, errMessage, Toast.LENGTH_LONG).show();
        }
      });

    } catch (Exception e) {
      pgr.setVisibility(View.INVISIBLE);
      signInbtn.setEnabled(true);
      Log.d("MainActivity", "checkCreds: " + e.getMessage());
    }

  }

  @Override
  protected void onStart() {
    super.onStart();
    new DatabaseConnector().checkAlreadyLogin(new MyStatListener() {
      @Override
      public void status(boolean status, Object obj) {
        if(status) startActivity(new Intent(SignInActivity.this, PhysicianHome.class));
      }

      @Override
      public void onFailure(String errMessage) {

      }
    });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    startActivity(new Intent(SignInActivity.this, MainActivity.class));
  }
}
