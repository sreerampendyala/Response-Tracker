package com.example.responsecounter.MiscellaneousActivites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.responsecounter.HomeActivites.PhysicianHome;
import com.example.responsecounter.HomeActivites.PhysiciansSubjectHome;
import com.example.responsecounter.HomeActivites.SubjectHome;
import com.example.responsecounter.R;
import com.example.util.DatabaseConnector;
import com.example.util.EntityClass;
import com.example.util.Interfaces.MyStatListener;
import com.example.util.Models.SubjectDetailModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsActivity extends AppCompatActivity {

  private CircleImageView profileImage;
  private ImageView camButton;
  private Uri imageUri = Uri.parse("");
  private Button submitbtn;
  private ActionBarDrawerToggle toggle;
  private EditText nameChanged;
  private Switch nameChangeSwitch;
  private static final int GALLERY_CODE = 1;

  private final String TAG = "SettingsPage";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    profileImage = findViewById(R.id.imgView_pic_settings);
    camButton = findViewById(R.id.imgview_cam_settings);
    submitbtn = findViewById(R.id.settings_submit);
    nameChanged = findViewById(R.id.updateName_settingsTb);
    nameChangeSwitch = findViewById(R.id.updateName_switchSettings);

    camButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
      }
    });

    nameChanged.setEnabled(false);

    nameChangeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) nameChanged.setEnabled(true);
        else {
          nameChanged.getText().clear();
          nameChanged.setEnabled(false);
        }
      }
    });

    submitbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(nameChangeSwitch.isChecked()) {
          updateName();
        }
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();
    getPictureOnStart();
  }

  private void updateName() {
    Log.d(TAG, "updateName: " + nameChanged.getText());
    SubjectDetailModel.getInstance().setSubjectName(nameChanged.getText().toString());
    new DatabaseConnector().updateName(new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {
        if(isSuccess) {
          Toast.makeText(settingsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
          Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              finish();
            }
          }, 1000);
        }
      }

      @Override
      public void onFailure(String errMessage) {
        Log.d(TAG, "onFailure: " + errMessage);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            finish();
          }
        }, 1000);
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
                .into(profileImage);
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
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
      if (data != null) {
        imageUri = data.getData();
        profileImage.setImageURI(imageUri);
        profileImage.setBackground(null);
        try {
          updateImageInDb(imageUri);
        } catch (Exception e) {
          Log.d("SubjectHome", "onActivityResult: during storage" + e.getMessage().toString());
          Toast.makeText(settingsActivity.this, "Error Updating picture", Toast.LENGTH_LONG).show();
          startActivity(new Intent(settingsActivity.this, PhysicianHome.class));
        }
      }
    }
  }

  private void updateImageInDb(Uri imageUri) {
    DatabaseConnector obj = new DatabaseConnector();
    obj.saveSubjectImage(imageUri, new MyStatListener() {
      @Override
      public void status(boolean isSuccess, Object obj) {
        Uri uri = Uri.parse(String.valueOf(obj));
        if (isSuccess) {
          Log.d(TAG, "statusAndUri: Image Uploaded Successfully: " + uri.toString());
        }
      }

      @Override
      public void onFailure(String errMessage) {
        Toast.makeText(settingsActivity.this, errMessage, Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if (EntityClass.getInstance().isSubject()) {
      startActivity(new Intent(settingsActivity.this, SubjectHome.class));
    } else {
      startActivity(new Intent(settingsActivity.this, PhysiciansSubjectHome.class));
    }
  }
}
