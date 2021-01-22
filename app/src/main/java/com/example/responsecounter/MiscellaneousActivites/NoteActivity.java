package com.example.responsecounter.MiscellaneousActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.responsecounter.HomeActivites.PhysiciansSubjectHome;
import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.NoteModel;
import com.example.util.Models.PhysicianDetailModel;
import com.example.util.Models.SubjectDetailModel;
import com.example.util.SaveSharedPreference;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.Help;

public class NoteActivity extends AppCompatActivity {

  private DrawerLayout dl;
  private ActionBarDrawerToggle toggle;
  private EditText myNotes;
  private ProgressBar pgr;

  private static final String TAG = "NoteActivity";

  private String path = "Notes";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_note);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    setNavigation();
    myNotes = findViewById(R.id.noteEditText);
    pgr = findViewById(R.id.noteIn_Progress);

    pgr.setVisibility(View.VISIBLE);
    getNotes();

  }

  private void setNavigation() {
    dl = (DrawerLayout)findViewById(R.id.dl_Note_Activity);
    toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
    toggle.setDrawerIndicatorEnabled(true);
    dl.addDrawerListener(toggle);
    toggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView nav_View = (NavigationView)findViewById(R.id.nav_View_Note);
    nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
          case (R.id.note) :{
            dl.closeDrawers();
            startActivity(new Intent(NoteActivity.this, NoteActivity.class));
            break;
          }

          case (R.id.myhelp) : {
            dl.closeDrawers();
            startActivity(new Intent(NoteActivity.this, Help.class));
            break;
          }

          case(R.id.signOut) : {
            saveNotes();
            dl.closeDrawers();
            new DatabaseConnector().firebaseSignOut();
            SaveSharedPreference.clearUserData(NoteActivity.this);
            if(EntityClass.getInstance().isSubject()) {
              EntityClass.getInstance().stopMyService(getApplicationContext());
            }
            startActivity(new Intent(NoteActivity.this, MainActivity.class));
            break;
          }

        }
        return true;
      }
    });
  }

  private void getNotes() {
    try {
      new DatabaseConnector().getNotes(path, new MyStatListener() {
        @Override
        public void status(boolean isSuccess, Object obj) {
          if(isSuccess) {
            Log.d(TAG, "status: ^^^^^^^^^^^^^^^^^^^^^" + ((NoteModel) obj).getContent());
            myNotes.setText(((NoteModel) obj).getContent());
            pgr.setVisibility(View.INVISIBLE);
          }
        }

        @Override
        public void onFailure(String errMessage) {
          Log.d(TAG, "onFailure: " + errMessage);
        }
      });
    } catch ( Exception ex) {
      Log.d(TAG, "getNotes: " + ex.getMessage());
    }
  }

  private void saveNotes() {

    try {
      NoteModel obj = new NoteModel();
      obj.setContent(myNotes.getText().toString());
      new DatabaseConnector().saveData(path, obj, new MyStatListener() {
        @Override
        public void status(boolean isSuccess, Object obj) {
          if(isSuccess) {
            Log.d(TAG, "status: Saved");
          }
        }

        @Override
        public void onFailure(String errMessage) {
          Log.d(TAG, "onFailure: " + errMessage);
        }
      });
    } catch (Exception ex) {
      Log.d(TAG, "saveNotes: " + ex.getMessage());
    }
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStop() {
    super.onStop();
    saveNotes();
  }

  @Override
  public void onBackPressed() {
    dl.closeDrawers();
    if(EntityClass.getInstance().isSubject()) {
      startActivity(new Intent(NoteActivity.this, SubjectHome.class));
    } else {
      startActivity(new Intent(NoteActivity.this, PhysiciansSubjectHome.class));
    }
  }
}
