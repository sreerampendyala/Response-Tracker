package com.example.responsecounter.TestActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.responsecounter.MainActivity;
import com.example.responsecounter.MiscellaneousActivites.NoteActivity;
import com.example.responsecounter.R;
import com.example.responsecounter.MiscellaneousActivites.ReportActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.Help;

public class SingleButtonActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_button);

        firebaseAuth = FirebaseAuth.getInstance();
        setNavigation();
    }

    private void setNavigation() {
        dl = (DrawerLayout)findViewById(R.id.dl_SingleButton_Activity);
        toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        toggle.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_View = (NavigationView)findViewById(R.id.nav_View_Single);
        nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case (R.id.duelButton): {
                        dl.closeDrawers();
                        startActivity(new Intent(SingleButtonActivity.this, DuelButtonActivity.class));
                        break;
                    }

                    case (R.id.singleButton) : {
                        dl.closeDrawers();
                        startActivity(new Intent(SingleButtonActivity.this, SingleButtonActivity.class));
                        break;
                    }

                    case (R.id.report) : {
                        dl.closeDrawers();
                        startActivity(new Intent(SingleButtonActivity.this, ReportActivity.class));
                        break;
                    }

                    case (R.id.note) :{
                        dl.closeDrawers();
                        startActivity(new Intent(SingleButtonActivity.this, NoteActivity.class));
                        break;
                    }

                    case (R.id.myhelp) : {
                        dl.closeDrawers();
                        startActivity(new Intent(SingleButtonActivity.this, Help.class));
                        break;
                    }

                    case(R.id.signOut) : {
                        dl.closeDrawers();
                        firebaseAuth.signOut();
                        startActivity(new Intent(SingleButtonActivity.this, MainActivity.class));
                        break;
                    }

                }
                return true;
            }
        });
    }
}
