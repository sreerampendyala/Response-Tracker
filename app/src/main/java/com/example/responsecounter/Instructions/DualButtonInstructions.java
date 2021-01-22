package com.example.responsecounter.Instructions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.responsecounter.R;
import com.example.responsecounter.TestActivities.DualButtonActivity;
import com.example.util.EntityClass;

public class DualButtonInstructions extends AppCompatActivity {

  private Button dual_button_practice, dual_button_take_test, dualButton_InstructionsBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dual_button_instructions);

    dual_button_practice = findViewById(R.id.dual_button_practice);
    dual_button_take_test = findViewById(R.id.dual_button_take_test);
    dualButton_InstructionsBtn = findViewById(R.id.dualButton_InstructionsBtn);

    dual_button_practice.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EntityClass.getInstance().setPractice(true);
        startActivity(new Intent(DualButtonInstructions.this, DualButtonActivity.class));
      }
    });

    dual_button_take_test.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EntityClass.getInstance().setPractice(false);
        startActivity(new Intent(DualButtonInstructions.this, DualButtonActivity.class));
      }
    });

    dualButton_InstructionsBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createDialogue();
      }
    });
  }

  /**
   * This Method is used to create a dialogue for instructions.
   */
  private void createDialogue() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    // Get the layout inflater
    builder.setMessage(R.string.two_button_tapping_test_instructions)
        // Add action buttons
        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
          }
        });
    builder.create().show();
  }
}