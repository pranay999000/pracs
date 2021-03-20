package com.example.pracs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText redgEditText = findViewById(R.id.editTextRegistrationNo);
        Button doneButton = findViewById(R.id.buttonDone);
        doneButton.setEnabled(false);

        redgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                doneButton.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) doneButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    private void checkSession() {
        SessionClass sessionClass = new SessionClass(RegistrationActivity.this);
        int userID = sessionClass.getUserID();
        String userBranch = sessionClass.getUserBranch();
        int userBatch = sessionClass.getUserBatch();

        if(userID != -1 && !userBranch.equals("null") && userBatch != -1) moveToMainActivity();
    }

    public void register(View view) {
        ConstraintLayout constraintLayout = findViewById(R.id.registrationLayout);
        EditText registerEditText = findViewById(R.id.editTextRegistrationNo);
        String registrationID = registerEditText.getText().toString().trim();

        if(registrationID.equals("")) Snackbar.make(constraintLayout, "Enter Registration ID", Snackbar.LENGTH_SHORT);

        int id = Integer.parseInt(registrationID);
        SessionClass sessionClass = new SessionClass(RegistrationActivity.this);
        User user;

        if(id < 1901201001 || id > 1901201116){
            Snackbar.make(constraintLayout, "Invalid registration ID", Snackbar.LENGTH_SHORT).show();
        }else if(id > 1901201000 && id <= 1901201015){
            user = new User(id, "civil", 19);
            sessionClass.saveSession(user);
        }else if(id > 1901201015 && id <= 1901201068){
            user = new User(id, "computer science", 19);
            sessionClass.saveSession(user);
        }else if(id > 1901201068 && id <= 1901201078){
            user = new User(id, "electrical electronics", 19);
            sessionClass.saveSession(user);
        }else if(id > 1901201078 && id <= 1901201094){
            user = new User(id, "electrical", 19);
            sessionClass.saveSession(user);
        }else if(id > 1901201094 && id <= 1901201096){
            user = new User(id, "electronics", 19);
            sessionClass.saveSession(user);
        }else if(id > 1901201096 && id <= 1901201116){
            user = new User(id, "mechanical", 19);
            sessionClass.saveSession(user);
        }

        moveToMainActivity();
    }

    private void moveToMainActivity() {
        Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }
}