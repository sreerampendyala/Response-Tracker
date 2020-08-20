package com.example.responsecounter.SignUpActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.ValidationInterfaces.SignUpInterface;
import com.example.util.Models.PhysicianDetailModel;
import com.example.util.Models.SubjectDetailModel;

public class SubjectSignUpActivity extends AppCompatActivity {

  private EditText subjectName;
  private EditText subjectAge;
  private EditText subjectEmail;
  private EditText subjectPwd;
  private EditText subjectRePwd;
  private EditText physicianEmail;

  private Button createAccountBtn;

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

        if (!TextUtils.equals(subjectRePwd.getText().toString(), subjectPwd.getText().toString())) {
          Toast.makeText(SubjectSignUpActivity.this, "Password miss match", Toast.LENGTH_LONG).show();
        } else if (!TextUtils.isEmpty(subjectAge.getText().toString())) {
          Toast.makeText(SubjectSignUpActivity.this, "Please fill in Age", Toast.LENGTH_LONG).show();
        } else {
          PhysicianDetailModel.getInstance().setPhysicianEmail(physicianEmail.getText().toString());
          SubjectDetailModel.getInstance().setSubjectAge(subjectAge.getText().toString());
          DatabaseConnector dbObj = new DatabaseConnector();
          dbObj.createUserAccount(subjectEmail.getText().toString(), subjectPwd.getText().toString(), subjectName.getText().toString(), new SignUpInterface() {
            @Override
            public void signUpStatus(boolean isSuccess) {

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
    });
  }
}
