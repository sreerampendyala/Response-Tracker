package com.example.responsecounter.SignUpActivities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.PhysicianHome;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.ValidateEmail;


public class PhysicianSignUpActivity extends AppCompatActivity {
    /**
     * User email is the edit text box for the user to enter email on the screen.
     */
    private EditText userEmail;
    /**
     * Pwd is the edit text box for the user to enter pwd on the screen.
     */
    private EditText pwd;
    /**
     * userName text box for the user to enter on the screen.
     */
    private EditText userName;
    private ProgressBar pgr;

    /**
     * Create account button for submission of the form.
     */
    private Button createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_sign_up);

        createAccount = findViewById(R.id.signUpBtn_signup);
        pgr = findViewById(R.id.create_Progress);
        userEmail = findViewById(R.id.username_signup_tb);
        userName = findViewById(R.id.userName_signup_tb);
        pwd = findViewById(R.id.pwd_signup_tb);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validates the email checks if it exists or not.
                new ValidateEmail(userEmail.getText().toString(), new MyStatListener() {
                    @Override
                    public void status(boolean isSuccess, Object obj) {
                        if(isSuccess) {
                            createEmailUserAccount(userEmail.getText().toString(), pwd.getText().toString(), userName.getText().toString());
                        }
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        Toast.makeText(PhysicianSignUpActivity.this, errMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * Creates a new email and account in the firebase for the user.
     * @param email user Email
     * @param password user Password
     * @param userId user Name.
     */
    private void createEmailUserAccount(String email, String password, final String  userId) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(userId)) {

            pgr.setVisibility(View.VISIBLE);

            DatabaseConnector obj = new DatabaseConnector();
            obj.createUserAccount(email, password, userId, new MyStatListener() {
                @Override
                public void status(boolean isSuccess, Object obj) {
                    if(isSuccess) {
                        pgr.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(PhysicianSignUpActivity.this, PhysicianHome.class));
                    }
                }

                @Override
                public void onFailure(String errMessage) {
                    pgr.setVisibility(View.INVISIBLE);
                    Toast.makeText(PhysicianSignUpActivity.this, errMessage, Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(PhysicianSignUpActivity.this, "Empty Feilds Not Allowed", Toast.LENGTH_LONG).show();
        }

    }
}
