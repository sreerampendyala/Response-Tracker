package com.example.responsecounter.HomeActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.responsecounter.MainActivity;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.DataInterfaces.SubjectList;
import com.example.util.Interfaces.ValidationInterfaces.SubjectInterface;
import com.example.util.Models.PhysicianDetailModel;
import com.example.util.Models.SubjectDetailModel;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class PhysicianHome extends AppCompatActivity {

    private int backButtonCount = 0;
    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;

    private Button submitBtn;
    private TextView userName;
    private AutoCompleteTextView autoCompleteTextView;

    private String subjectName;
    private final String TAG = "PhysicianHome";
    private ProgressBar pgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_home);

        setUpNavigation();
        updateDropDown();
        submitBtn = findViewById(R.id.submit_btn_physicianHome);
        userName = findViewById(R.id.username_tv);
        pgr = findViewById(R.id.physicianHome_Progress);

        userName.setText(PhysicianDetailModel.getInstance().getPhysicianName());
        SubjectDetailModel.getInstance().setSubjectName("");
        SubjectDetailModel.getInstance().setSubjectEmail("");

        autoCompleteTextView.getText().clear();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(autoCompleteTextView.getText().toString())) {
                    pgr.setVisibility(View.VISIBLE);
                    submitBtn.setEnabled(false);
                    DatabaseConnector databaseConnector = new DatabaseConnector();
                    databaseConnector.checkExistingSubject(autoCompleteTextView.getText().toString(), subjectName, new SubjectInterface() {
                        @Override
                        public void subjectExistOrCreated(boolean isSuccess) {
                            if(isSuccess) {
                                pgr.setVisibility(View.INVISIBLE);
                                submitBtn.setEnabled(true);
                                startActivity(new Intent(PhysicianHome.this, PhysiciansSubjectHome.class));
                            }
                        }

                        @Override
                        public void onFailure(String errMessage) {
                            pgr.setVisibility(View.INVISIBLE);
                            submitBtn.setEnabled(true);
                        }
                    });
                }

            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().split("email:").length == 2) {
                    subjectName = s.toString().split("email:")[0];
                    Editable text = new SpannableStringBuilder(s.toString().split("email:")[1]);
                    s.replace(0, s.length(), text);
                }
            }
        });
    }

    /**
     * This method is used to set the navbar.
     */
    private void setUpNavigation() {

        dl = (DrawerLayout)findViewById(R.id.dl_physician_home);
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
                        startActivity(new Intent(PhysicianHome.this, MainActivity.class));
                        break;
                    }
                }
                return true;
            }
        });
    }

    /**
     * This method is used to update the drop down.
     */
    private void updateDropDown(){
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.dropDownTextView_physicianHome);


        DatabaseConnector databaseConnector = new DatabaseConnector();
        databaseConnector.getSubjectsList(new SubjectList() {
            @Override
            public void getSubjectListStatus(boolean isSuccess, List<String> subjectData) {
                if(isSuccess && !subjectData.isEmpty()){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PhysicianHome.this, android.R.layout.simple_list_item_1, subjectData);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    autoCompleteTextView.setThreshold(1);
                    autoCompleteTextView.setAdapter(adapter);

                    String[] items = new String[subjectData.size()];
                    for(int i = 0; i< subjectData.size(); i++) {
                        items[i] = subjectData.get(i);
                    }
                }
            }

            @Override
            public void onFailure(String errMessage) {
                //
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
