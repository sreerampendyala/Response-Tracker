package com.example.responsecounter.MiscellaneousActivites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.Interfaces.ValidationInterfaces.PasswordResetInterface;

public class PasswordReset extends AppCompatActivity {

  private AppCompatButton submitBtn;
  private EditText emailBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_password_reset);

    submitBtn = findViewById(R.id.password_reset_submit_btn);
    emailBox = findViewById(R.id.password_reset_email_tb);

    submitBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(TextUtils.isEmpty(emailBox.getText().toString())) {
          Toast.makeText(PasswordReset.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
        }
        else {
          new DatabaseConnector().forgotPassword(emailBox.getText().toString(), new PasswordResetInterface() {
            @Override
            public void status(boolean isSuccess, String msg) {
              if(isSuccess) {
                Toast.makeText(PasswordReset.this, msg, Toast.LENGTH_LONG).show();
                finish();
              }
              else Toast.makeText(PasswordReset.this, msg, Toast.LENGTH_LONG).show();
            }
          });
        }
      }
    });

  }
}
