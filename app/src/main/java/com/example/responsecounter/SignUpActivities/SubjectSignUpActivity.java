package com.example.responsecounter.SignUpActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.PhysicianDetailModel;
import com.example.util.Models.SubjectDetailModel;
import com.example.util.ValidateEmail;

public class SubjectSignUpActivity extends AppCompatActivity {

  /**
   * Patient name
   */
  private EditText subjectName;
  /**
   * Patient age
   */
  private EditText subjectAge;
  /**
   * Patient Email
   */
  private EditText subjectEmail;
  /**
   * Patient Password.
   */
  private EditText subjectPwd;
  /**
   * Patient Password re enter.
   */
  private EditText subjectRePwd;
  /**
   * Physician email, to check if the physician exists.
   */
  private EditText physicianEmail;
  /**
   * Create account button.
   */
  private Button createAccountBtn;
  /**
   * Boolean to check if the email is valid or not, exists or not(not in the fire-base but in it's domain.
   */
  private boolean validEmail = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_subject_sign_up);

    subjectName = findViewById(R.id.enterName_Subject_tv);
    subjectAge = findViewById(R.id.enterAge_Subject_tv);
    subjectEmail = findViewById(R.id.enterEmail_subject_tv);
    subjectPwd = findViewById(R.id.enterPwd_Subject_tv);
    subjectRePwd = findViewById(R.id.reEnterPwd_Subject_tv);
    physicianEmail = findViewById(R.id.physicianEmail_Subject_tv);

    createAccountBtn = findViewById(R.id.createAccount_Subject_btn);

    createAccountBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        ValidateEmail obj = new ValidateEmail(subjectEmail.getText().toString(), new MyStatListener() {
          @Override
          public void status(boolean isSuccess, Object obj) {
            if(isSuccess) validEmail = true;
          }

          @Override
          public void onFailure(String errMessage) {
            Toast.makeText(SubjectSignUpActivity.this, errMessage, Toast.LENGTH_LONG).show();
          }
        });

        if(validEmail) {
          if (!TextUtils.equals(subjectRePwd.getText().toString(), subjectPwd.getText().toString())) {
            Toast.makeText(SubjectSignUpActivity.this, "Password miss match", Toast.LENGTH_LONG).show();
          } else if (TextUtils.isEmpty(subjectAge.getText().toString())) {
            Toast.makeText(SubjectSignUpActivity.this, "Please fill in Age", Toast.LENGTH_LONG).show();
          } else {
            PhysicianDetailModel.getInstance().setPhysicianEmail(physicianEmail.getText().toString());
            SubjectDetailModel.getInstance().setSubjectAge(subjectAge.getText().toString());
            DatabaseConnector dbObj = new DatabaseConnector();
            dbObj.createUserAccount(subjectEmail.getText().toString(), subjectPwd.getText().toString(), subjectName.getText().toString(), new MyStatListener() {
              @Override
              public void status(boolean isSuccess, Object obj) {

                if (isSuccess) {
                  startActivity(new Intent(SubjectSignUpActivity.this, SubjectHome.class));
                }
              }

              @Override
              public void onFailure(String errMessage) {
                Toast.makeText(SubjectSignUpActivity.this, errMessage, Toast.LENGTH_LONG).show();
              }
            });
          }
        }
      }
    });
  }
}
