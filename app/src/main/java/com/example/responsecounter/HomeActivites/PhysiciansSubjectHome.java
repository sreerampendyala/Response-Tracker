package com.example.responsecounter.HomeActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.responsecounter.MainActivity;
import com.example.responsecounter.MiscellaneousActivites.HelpActivity;
import com.example.responsecounter.MiscellaneousActivites.NoteActivity;
import com.example.responsecounter.MiscellaneousActivites.ReportActivity;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.PhysicianChoiceModel;
import com.example.util.Models.SubjectDetailModel;
import com.example.util.PhysicianChoiceAdapter;
import com.example.util.SaveSharedPreference;
import com.example.util.SetupOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.rpc.Help;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PhysiciansSubjectHome extends AppCompatActivity {

  private RecyclerView recyclerView;
  private RecyclerView.LayoutManager layoutManager;
  private List<PhysicianChoiceModel> physicianChoiceList = new ArrayList<>();

  private AppCompatButton submitBtn;
  private ProgressBar pgr;
  private DrawerLayout drawerLayout;
  private ActionBarDrawerToggle toggle;
  private TextView subjectDetails;

  private ImageView image;
  private final String      TAG = "PhysicianSubjectHome";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_physicians_subject_home);

    submitBtn = findViewById(R.id.physician_patient_home_submit_btn);
    pgr = findViewById(R.id.physicianChoiceUpdatePgr);
    subjectDetails = findViewById(R.id.physician_patientDetailsTextView);
    image = findViewById(R.id.imgView_pic_physician_patientHome);
    setNavigation();

    String data = "Name:\t\t" + SubjectDetailModel.getInstance().getSubjectName() + "\n" +
        "Email:\t\t" + SubjectDetailModel.getInstance().getSubjectEmail() + "\n" +
        "Age:\t\t" + SubjectDetailModel.getInstance().getSubjectAge() + "\n";
    subjectDetails.setText(data);

    submitBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        pgr.setVisibility(View.VISIBLE);
        new DatabaseConnector().updatePhysicianControl(new MyStatListener() {
          @Override
          public void status(boolean isSuccess, Object obj) {
            if(isSuccess) Toast.makeText(PhysiciansSubjectHome.this, "Updated", Toast.LENGTH_LONG).show();
            pgr.setVisibility(View.INVISIBLE);
          }

          @Override
          public void onFailure(String errMessage) {
            Toast.makeText(PhysiciansSubjectHome.this, errMessage, Toast.LENGTH_LONG).show();
            pgr.setVisibility(View.INVISIBLE);
          }
        });
      }
    });
  }

  private void getPictureOnStart() {
    try {
      DatabaseConnector obj = new DatabaseConnector();
      obj.getSubjectImage(new MyStatListener() {
        @Override
        public void status(boolean isSuccess, Object obj) {
          Uri uri = Uri.parse(String.valueOf(obj));
          if (isSuccess) {
            Picasso.get().load(uri).placeholder(R.drawable.user_picture_24dp)
                .fit()
                .into(image);
          }
        }

        @Override
        public void onFailure(String errMessage) {
          //
        }

      });

    } catch (Exception ex) {
      Log.d(TAG, "getPictureOnStart: " + ex.getMessage());
    }

  }

  @Override
  protected void onStart() {
    super.onStart();
    getPictureOnStart();
    //Get data from db and update physician Choice List.

    new DatabaseConnector().getPhysicianControl(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object myObj) {
        if (isSuccess) {
          physicianChoiceList = EntityClass.getInstance().getPhysicianChoiceList();
          setRecyclerView();
        } else {
          for (SetupOptions lable : SetupOptions.values()) {
            PhysicianChoiceModel obj = new PhysicianChoiceModel();
            obj.setLable(EntityClass.getInstance().getLbl(lable));
            obj.setValue(false);
            physicianChoiceList.add(obj);
          }
          EntityClass.getInstance().setPhysicianChoiceList(physicianChoiceList);
          setRecyclerView();
        }
      }

      @Override
      public void onFailure(String errMessage) {
        Toast.makeText(PhysiciansSubjectHome.this, errMessage, Toast.LENGTH_LONG).show();
      }
    });


  }

  private void setRecyclerView() {
    recyclerView = findViewById(R.id.recycler_physician_patient_view);

    recyclerView.setHasFixedSize(true);

    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);

    PhysicianChoiceAdapter physicianChoiceAdapter = new PhysicianChoiceAdapter(physicianChoiceList);
    recyclerView.setHasFixedSize(true);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(physicianChoiceAdapter);

  }

  private void setNavigation() {
    drawerLayout = (DrawerLayout) findViewById(R.id.physician_subject_home_dl);
    toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
    toggle.setDrawerIndicatorEnabled(true);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView nav_View = (NavigationView) findViewById(R.id.subject_home_physician_nav);
    nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
          case (R.id.note): {
            drawerLayout.closeDrawers();
            startActivity(new Intent(PhysiciansSubjectHome.this, NoteActivity.class));
            break;
          }

          case (R.id.report): {
            drawerLayout.closeDrawers();
            startActivity(new Intent(PhysiciansSubjectHome.this, ReportActivity.class));
            break;
          }

          case (R.id.myhelp): {
            drawerLayout.closeDrawers();
            startActivity(new Intent(PhysiciansSubjectHome.this, HelpActivity.class));
            break;
          }

          case (R.id.signOut): {
            drawerLayout.closeDrawers();
            SaveSharedPreference.clearUserData(PhysiciansSubjectHome.this);
            new DatabaseConnector().firebaseSignOut();
            startActivity(new Intent(PhysiciansSubjectHome.this, MainActivity.class));
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
}
