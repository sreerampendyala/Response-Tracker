package com.example.responsecounter;

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

import com.example.util.Interfaces.CheckLoggedInInterface;
import com.example.util.Interfaces.CredValidationInterface;
import com.example.util.DatabaseConnector;


public class MainActivity extends AppCompatActivity {

    private Button signInbtn;
    private TextView signUp;
    private AutoCompleteTextView email;
    private TextView pwd;
    private ProgressBar pgr;
    private final String TAG = "MainActivity";

    private int backButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInbtn = findViewById(R.id.signUpBtn);
        signUp = findViewById(R.id.signIn_tv);
        email = findViewById(R.id.username_tb);
        pwd = findViewById(R.id.pwd_tb);
        pgr = findViewById(R.id.signIn_Progress);

        new DatabaseConnector().checkAlreadyLogin(new CheckLoggedInInterface() {
            @Override
            public void isLoggedIn(boolean status) {
                if(status) startActivity(new Intent(MainActivity.this, AppHomeActivity.class));
            }
        });

        signInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(pwd.getText().toString())) {
                    pgr.setVisibility(View.VISIBLE);
                    checkCreds(email.getText().toString(), pwd.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Feilds Should not be empty", Toast.LENGTH_LONG).show();
                    Log.d("MainActivity", "Creds error");
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
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
                        startActivity(new Intent(MainActivity.this, AppHomeActivity.class));
                    }
                }

                @Override
                public void onFailure(String errMessage) {
                    pgr.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onFailure: Failed validating credentials from the database" + errMessage);
                    Toast.makeText(MainActivity.this, errMessage, Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            pgr.setVisibility(View.INVISIBLE);
            Log.d("MainActivity", "checkCreds: " + e.getMessage());
        }

    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)  {
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
