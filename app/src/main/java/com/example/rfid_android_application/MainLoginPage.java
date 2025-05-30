package com.example.rfid_android_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainLoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences studentPrefs = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        SharedPreferences teacherPrefs = getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);


        if (studentPrefs.contains("student_name") && studentPrefs.contains("student_CUIN")) {
            navigateToHome();
            return;
        }

        if (teacherPrefs.contains("teacher_name") && teacherPrefs.contains("teacher_id")) {
            navigateToHome();
            return;
        }


        setContentView(R.layout.activity_main_login_page);

        Button studentLogin = findViewById(R.id.MainLoginPageStudentButton);
        Button teacherLogin = findViewById(R.id.MainLoginPageTeacherButton);

        studentLogin.setOnClickListener(v -> startActivity(new Intent(MainLoginPage.this, LoginStudentPage.class)));
        teacherLogin.setOnClickListener(v -> startActivity(new Intent(MainLoginPage.this, LoginTeacherPage.class)));
    }

    private void navigateToHome() {
        startActivity(new Intent(MainLoginPage.this, Home_Page.class));
        finish();
    }
}
