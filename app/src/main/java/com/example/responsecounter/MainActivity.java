package com.example.responsecounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.responsecounter.SignInActivities.SignInActivity;
import com.example.util.EntityClass;
import com.example.util.Models.PhysicianDetailModel;


public class MainActivity extends AppCompatActivity {
    private Button isPatient;
    private Button isPhyssician;

    private int backButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPatient = findViewById(R.id.button_isPatient);
        isPhyssician = findViewById(R.id.button_isPhysician);

        isPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntityClass.getInstance().setSubject(true);
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });


        isPhyssician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntityClass.getInstance().setSubject(false);
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PhysicianDetailModel.getInstance().setPhysicianEmail("");
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
