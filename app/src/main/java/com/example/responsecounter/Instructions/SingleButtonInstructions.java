package com.example.responsecounter.Instructions;

import androidx.appcompat.app.AppCompatActivity;
import com.example.responsecounter.R;
import com.example.responsecounter.TestActivities.SingleButtonActivity;
import com.example.util.EntityClass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SingleButtonInstructions extends AppCompatActivity {
  private Button dual_button_practice, dual_button_take_test;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_single_button_instructions);

    dual_button_practice = findViewById(R.id.single_button_practice);
    dual_button_take_test = findViewById(R.id.single_button_take_test);

    dual_button_practice.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EntityClass.getInstance().setPractice(true);
        startActivity(new Intent(SingleButtonInstructions.this, SingleButtonActivity.class));
      }
    });

    dual_button_take_test.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EntityClass.getInstance().setPractice(false);
        startActivity(new Intent(SingleButtonInstructions.this, SingleButtonActivity.class));
      }
    });
  }
}