package com.example.responsecounter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.util.DatabaseConnector;
import com.example.util.Interfaces.DataInterface;
import com.example.util.Models.TapModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.Help;


public class DuelButtonActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;

    private CountDownTimer timer = null;
    private EditText timeBox;
    private Button btn1, btn2, startBtn;
    private int count1 = 0, count2 = 0;
    private TextView txtView;
    private final String TAG = "DuelButtonActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collRef = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel_button);

        firebaseAuth = FirebaseAuth.getInstance();
        setNavigation();

        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setEnabled(false);
                txtView = findViewById(R.id.resultTb);
                txtView.setText("Total Count: 0");
                count1 = 0;
                count2 = 0;
                Timer();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public void Timer() {
        timeBox = (EditText)findViewById(R.id.timerBox);
        btn1 = findViewById(R.id.buttonCount1);
        btn2 = findViewById(R.id.buttonCount2);
        startBtn = findViewById(R.id.startBtn);

        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeBox.setText("Remaining: "+ millisUntilFinished/1000);


                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count1 ++;
                    }
                });

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count2++;
                    }
                });

            }

            @Override
            public void onFinish() {
                startBtn.setEnabled(true);
                timeBox.setText("Finished !!");
                final TapModel record = new TapModel();
                record.setCount(count1 + count2);
                txtView = findViewById(R.id.resultTb);
                txtView.setText("Total Count: " + record.getCount());

                try {
                    DatabaseConnector dbObj = new DatabaseConnector();
                    dbObj.saveData("SubjectData/DoubleTapData/", record, new DataInterface() {
                        @Override
                        public void successStatus(boolean isSuccess) {

                        }

                        @Override
                        public void onFailure(String errMessage) {

                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "onFinish: " + e.getClass().toString()+ " " + e.getMessage());
                }

            }
        };
        timer.start();
    }

    private void setNavigation() {

        dl = (DrawerLayout)findViewById(R.id.dl_duel_Button_Activity);
        toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        toggle.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_View = (NavigationView)findViewById(R.id.nav_View_DuelButton);
        nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case (R.id.duelButton): {
                        dl.closeDrawers();
                        startActivity(new Intent(DuelButtonActivity.this, DuelButtonActivity.class));
                        break;
                    }

                    case (R.id.singleButton) : {
                        dl.closeDrawers();
                        startActivity(new Intent(DuelButtonActivity.this, SingleButtonActivity.class));
                        break;
                    }

                    case (R.id.report) : {
                        dl.closeDrawers();
                        startActivity(new Intent(DuelButtonActivity.this, ReportActivity.class));
                        break;
                    }

                    case (R.id.note) :{
                        dl.closeDrawers();
                        startActivity(new Intent(DuelButtonActivity.this, NoteActivity.class));
                        break;
                    }

                    case (R.id.myhelp) : {
                        dl.closeDrawers();
                        startActivity(new Intent(DuelButtonActivity.this, Help.class));
                        break;
                    }

                    case(R.id.signOut) : {
                        dl.closeDrawers();
                        new DatabaseConnector().firebaseSignOut();
                        startActivity(new Intent(DuelButtonActivity.this, MainActivity.class));
                        break;
                    }

                }
                return true;
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        dl.closeDrawers();
        startActivity(new Intent(DuelButtonActivity.this, HomeActivity.class));
    }
}
