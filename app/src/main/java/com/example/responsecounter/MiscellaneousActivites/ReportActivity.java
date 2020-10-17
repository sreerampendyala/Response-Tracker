package com.example.responsecounter.MiscellaneousActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.Help;
import com.jjoe64.graphview.GraphView;

public class ReportActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        firebaseAuth = FirebaseAuth.getInstance();
        setNavigation();



    }

    private void setNavigation() {
        dl = (DrawerLayout)findViewById(R.id.dl_Report_Activity);
        toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        toggle.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_View = (NavigationView)findViewById(R.id.nav_View_report);
        nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case (R.id.note) :{
                        dl.closeDrawers();
                        startActivity(new Intent(ReportActivity.this, NoteActivity.class));
                        break;
                    }

                    case (R.id.myhelp) : {
                        dl.closeDrawers();
                        startActivity(new Intent(ReportActivity.this, Help.class));
                        break;
                    }

                    case(R.id.signOut) : {
                        dl.closeDrawers();
                        firebaseAuth.signOut();
                        startActivity(new Intent(ReportActivity.this, MainActivity.class));
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
        startActivity(new Intent(ReportActivity.this, SubjectHome.class));
    }

    private void drawGraph() {
        final GraphView graph = (GraphView) findViewById(R.id.graph);
    }
}
