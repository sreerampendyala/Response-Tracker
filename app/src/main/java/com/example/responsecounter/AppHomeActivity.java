package com.example.responsecounter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.SubjectInterface;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AppHomeActivity extends AppCompatActivity {

    private int backButtonCount = 0;
    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;

    private Button existingSubject;
    private Button newSubject;
    private TextView subjectName;
    private TextView subjectId;
    private TextView userName;
    private final String TAG = "AppHomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_home);

        setUpNavigation();

        existingSubject = findViewById(R.id.existingUsr_btn);
        newSubject = findViewById(R.id.newUser_btn);
        subjectName = findViewById(R.id.subjectName_tb);
        subjectId = findViewById(R.id.subjectid_tb);
        userName = findViewById(R.id.username_tv);


        EntityClass obj = EntityClass.getInstance();
        userName.setText(obj.getUserName());
        obj.setSubjectName("");
        obj.setSubjectEmail("");

        existingSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(subjectId.getText().toString()) || TextUtils.isEmpty(subjectName.getText().toString())) {
                    Toast.makeText(AppHomeActivity.this, "Feilds cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseConnector obj = new DatabaseConnector();
                    obj.checkExistingSubject(subjectId.getText().toString(), subjectName.getText().toString(), new SubjectInterface() {
                        @Override
                        public void subjectExistOrCreated(boolean isSuccess) {
                            if(isSuccess) {
                                startActivity(new Intent(AppHomeActivity.this, HomeActivity.class));
                            }
                        }

                        @Override
                        public void onFailure(String errMessage) {
                            Toast.makeText(AppHomeActivity.this, errMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


        newSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(subjectId.getText().toString()) || TextUtils.isEmpty(subjectName.getText().toString())) {
                    Toast.makeText(AppHomeActivity.this, "Feilds cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseConnector db = new DatabaseConnector();
                    db.createNewSubject(subjectId.getText().toString(), subjectName.getText().toString(), new SubjectInterface() {
                        @Override
                        public void subjectExistOrCreated(boolean isSuccess) {
                            if(isSuccess) {
                                startActivity(new Intent(AppHomeActivity.this, HomeActivity.class));
                            }
                        }

                        @Override
                        public void onFailure(String errMessage) {
                            Toast.makeText(AppHomeActivity.this, errMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


    }


    private void setUpNavigation() {

        dl = (DrawerLayout)findViewById(R.id.dl_app_home);
        toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        toggle.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_View = (NavigationView)findViewById(R.id.nav_View_App_Home);
        nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {

                    case(R.id.signOut) : {
                        dl.closeDrawers();
                        new DatabaseConnector().firebaseSignOut();
                        startActivity(new Intent(AppHomeActivity.this, MainActivity.class));
                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
