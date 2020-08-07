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
import com.example.responsecounter.R;
import com.example.responsecounter.SignUpActivities.PhysicianSignUpActivity;
import com.example.responsecounter.SignUpActivities.SubjectSignUpActivity;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.ValidationInterfaces.CheckLoggedInInterface;
import com.example.util.Interfaces.ValidationInterfaces.CredValidationInterface;

public class SignInActivity extends AppCompatActivity {

    private Button signInbtn;
    private TextView signUp;
    private AutoCompleteTextView email;
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

    private void checkCreds(String email, String password) {

        try {
            Log.d("MainActivity", "checkCreds:  after constructor");
            DatabaseConnector obj = new DatabaseConnector();
            obj.validateLogin(email, password, new CredValidationInterface() {
                @Override
                public void onSuccessValidatingCredentials(boolean isSuccess) {
                    if(isSuccess) {
                        pgr.setVisibility(View.INVISIBLE);
                        signInbtn.setEnabled(true);
                        if(EntityClass.getInstance().isSubject()) {
                            startActivity(new Intent(SignInActivity.this, SubjectHome.class));
                        } else startActivity(new Intent(SignInActivity.this, PhysicianHome.class));
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
        new DatabaseConnector().checkAlreadyLogin(new CheckLoggedInInterface() {
            @Override
            public void isLoggedIn(boolean status) {
                if(status) startActivity(new Intent(SignInActivity.this, PhysicianHome.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
    }
}
