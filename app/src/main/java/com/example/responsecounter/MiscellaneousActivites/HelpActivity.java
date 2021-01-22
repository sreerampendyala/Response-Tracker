package com.example.responsecounter.MiscellaneousActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.responsecounter.HomeActivites.PhysiciansSubjectHome;
import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.SaveSharedPreference;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.rpc.Help;

public class HelpActivity extends AppCompatActivity {

  private DrawerLayout dl;
  private ActionBarDrawerToggle toggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);

    setNavigation();



  }

  private void setNavigation() {
    dl = (DrawerLayout)findViewById(R.id.dl_Help_Activity);
    toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
    toggle.setDrawerIndicatorEnabled(true);
    dl.addDrawerListener(toggle);
    toggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView nav_View = (NavigationView)findViewById(R.id.nav_View_Help);
    nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
          case (R.id.note) :{
            dl.closeDrawers();
            startActivity(new Intent(HelpActivity.this, NoteActivity.class));
            break;
          }

          case (R.id.myhelp) : {
            dl.closeDrawers();
            startActivity(new Intent(HelpActivity.this, Help.class));
            break;
          }

          case(R.id.signOut) : {
            dl.closeDrawers();
            new DatabaseConnector().firebaseSignOut();
            SaveSharedPreference.clearUserData(HelpActivity.this);
            if(EntityClass.getInstance().isSubject()) {
              EntityClass.getInstance().stopMyService(getApplicationContext());
            }
            startActivity(new Intent(HelpActivity.this, MainActivity.class));
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
    dl.closeDrawers();
    if(EntityClass.getInstance().isSubject()) {
      startActivity(new Intent(HelpActivity.this, SubjectHome.class));
    } else {
      startActivity(new Intent(HelpActivity.this, PhysiciansSubjectHome.class));
    }
  }
}
