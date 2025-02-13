package com.example.rfid_android_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home_Page extends AppCompatActivity {

    Button logoutButton;
    SharedPreferences studentPrefs, teacherPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        logoutButton = findViewById(R.id.logoutButton);

        studentPrefs = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        teacherPrefs = getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void logoutUser() {

        SharedPreferences.Editor studentEditor = studentPrefs.edit();
        studentEditor.clear();
        studentEditor.apply();

        SharedPreferences.Editor teacherEditor = teacherPrefs.edit();
        teacherEditor.clear();
        teacherEditor.apply();

        Intent intent = new Intent(Home_Page.this, MainLoginPage.class);
        startActivity(intent);
        finish();
    }
}
