package com.example.responsecounter.HomeActivites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.responsecounter.TestActivities.DuelButtonActivity;
import com.example.responsecounter.MainActivity;
import com.example.responsecounter.MiscellaneousActivites.NoteActivity;
import com.example.responsecounter.R;
import com.example.responsecounter.MiscellaneousActivites.ReportActivity;
import com.example.responsecounter.TestActivities.SingleButtonActivity;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.DataInterfaces.ImageInterface;
import com.google.android.material.navigation.NavigationView;
import com.google.rpc.Help;
import com.squareup.picasso.Picasso;


public class SubjectHome extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;
    private int backButtonCount = 0;
    private TextView name;
    private TextView subId;
    private TextView change_pricture;
    private ImageView camButton;
    private ImageView image;
    private static final int GALLERY_CODE = 1;
    private Uri imageUri;

    private final String TAG = "SubjectHome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_home);

        setNavigation();

        name = (TextView) findViewById(R.id.name_tv_home);
        subId = (TextView)findViewById(R.id.changePic_tv5);
        change_pricture = (TextView)findViewById(R.id.changePic_tv);

        name.setText(EntityClass.getInstance().getSubjectName());
        subId.setText(EntityClass.getInstance().getSubjectEmail());

        camButton = findViewById(R.id.imgview_cam_home);
        image = findViewById(R.id.imgView_pic_home);



        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT );
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getPictureOnStart();
    }

    private void getPictureOnStart() {

        try {
            DatabaseConnector obj = new DatabaseConnector();
            obj.getSubjectImage(new ImageInterface() {
                @Override
                public void statusAndUri(boolean isSuccess, Uri uri) {
                    if(isSuccess) {
                        Picasso.get().load(uri).placeholder(R.drawable.background)
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

    private void setNavigation() {
        dl = (DrawerLayout)findViewById(R.id.dl_Subject_Home_Activity);
        toggle = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        toggle.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_View = (NavigationView)findViewById(R.id.nav_View_Home);
        nav_View.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case (R.id.duelButton): {
                        dl.closeDrawers();
                        startActivity(new Intent(SubjectHome.this, DuelButtonActivity.class));
                        break;
                    }

                    case (R.id.singleButton) : {
                        dl.closeDrawers();
                        startActivity(new Intent(SubjectHome.this, SingleButtonActivity.class));
                        break;
                    }

                    case (R.id.report) : {
                        dl.closeDrawers();
                        startActivity(new Intent(SubjectHome.this, ReportActivity.class));
                        break;
                    }

                    case (R.id.note) :{
                        dl.closeDrawers();
                        startActivity(new Intent(SubjectHome.this, NoteActivity.class));
                        break;
                    }

                    case (R.id.myhelp) : {
                        dl.closeDrawers();
                        startActivity(new Intent(SubjectHome.this, Help.class));
                        break;
                    }

                    case(R.id.signOut) : {
                        dl.closeDrawers();
                        new DatabaseConnector().firebaseSignOut();
                        startActivity(new Intent(SubjectHome.this, MainActivity.class));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                imageUri = data.getData();
                image.setImageURI(imageUri);
                image.setBackground(null);
                try {
                    updateDb(imageUri);
                } catch (Exception e) {
                    Log.d("SubjectHome", "onActivityResult: during storage" + e.getMessage().toString());
                    Toast.makeText(SubjectHome.this, "Error Updating picture", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SubjectHome.this, PhysicianHome.class));
                }

            }
        }
    }

    private void updateDb(Uri imageUri) {

        DatabaseConnector obj = new DatabaseConnector();
        obj.saveSubjectImage(imageUri, new ImageInterface() {
            @Override
            public void statusAndUri(boolean isSuccess, Uri uri) {
                if(isSuccess) {
                    Log.d(TAG, "statusAndUri: Image Uploaded Successfully: " + uri.toString());
                }
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(SubjectHome.this, errMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)  {
            startActivity(new Intent(SubjectHome.this, PhysicianHome.class));
        } else {
            Toast.makeText(this, "Press the back button once again to go to APP Home.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

}
