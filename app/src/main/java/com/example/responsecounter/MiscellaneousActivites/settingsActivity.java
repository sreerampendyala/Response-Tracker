package com.example.responsecounter.MiscellaneousActivites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.PhysicianHome;
import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.Interfaces.DataInterfaces.ImageInterface;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private ImageView camButton;
    private Uri imageUri;
    private ActionBarDrawerToggle toggle;
    private static final int GALLERY_CODE = 1;

    private final String TAG = "SettingsPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profileImage = findViewById(R.id.imgView_pic_settings);
        camButton = findViewById(R.id.imgview_cam_settings);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);
                profileImage.setBackground(null);
                try {
                    updateDb(imageUri);
                } catch (Exception e) {
                    Log.d("SubjectHome", "onActivityResult: during storage" + e.getMessage().toString());
                    Toast.makeText(settingsActivity.this, "Error Updating picture", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(settingsActivity.this, PhysicianHome.class));
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
                Toast.makeText(settingsActivity.this, errMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
