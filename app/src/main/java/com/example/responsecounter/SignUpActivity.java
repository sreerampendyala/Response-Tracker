package com.example.responsecounter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.util.DatabaseConnector;
import com.example.util.Interfaces.SignUpInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText pwd;
    private EditText userId;
    private ProgressBar pgr;
    private FirebaseUser currentUser;
    private Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        createAccount = findViewById(R.id.signUpBtn_signup);
        pgr = findViewById(R.id.create_Progress);
        userEmail = findViewById(R.id.username_signup_tb);
        userId = findViewById(R.id.uerId_signup_tb);
        pwd = findViewById(R.id.pwd_signup_tb);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                createEmailUserAccount(userEmail.getText().toString(), pwd.getText().toString(), userId.getText().toString());
            }
        });
    }

    private void createEmailUserAccount(String email, String password, final String  userId) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(userId)) {

            pgr.setVisibility(View.VISIBLE);

            DatabaseConnector obj = new DatabaseConnector();
            obj.createUserAccount(email, password, userId, new SignUpInterface() {
                @Override
                public void signUpStatus(boolean isSuccess) {
                    if(isSuccess) {
                        pgr.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(SignUpActivity.this, AppHomeActivity.class));
                    }
                }

                @Override
                public void onFailure(String errMessage) {
                    pgr.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUpActivity.this, errMessage, Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(SignUpActivity.this, "Empty Feilds Not Allowed", Toast.LENGTH_LONG).show();
        }

    }
}
