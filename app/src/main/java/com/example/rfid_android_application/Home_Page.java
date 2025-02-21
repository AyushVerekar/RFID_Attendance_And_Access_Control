package com.example.rfid_android_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Home_Page extends AppCompatActivity {

    Button logoutButton, timetableButton, attendanceButton;
    SharedPreferences studentPrefs, teacherPrefs;
    TextView userName, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        logoutButton = findViewById(R.id.logoutButton);
        timetableButton = findViewById(R.id.HomeTimeTableButton);
        attendanceButton = findViewById(R.id.HomeAttendanceButton);
        userName = findViewById(R.id.UserName);
        userId = findViewById(R.id.UserId);

        studentPrefs = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        teacherPrefs = getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);

        // Display user name and ID
        if (teacherPrefs.contains("teacher_name") && teacherPrefs.contains("teacher_id")) {
            String name = teacherPrefs.getString("teacher_name", "Unknown Teacher").toUpperCase();
            String id = teacherPrefs.getString("teacher_id", "Unknown ID");
            userName.setText("NAME: " + name);
            userId.setText("ID: " + id);
        } else if (studentPrefs.contains("student_name") && studentPrefs.contains("student_CUIN")) {
            String name = studentPrefs.getString("student_name", "Unknown Student").toUpperCase();
            String cuin = studentPrefs.getString("student_CUIN", "Unknown CUIN");
            userName.setText("NAME: " + name);
            userId.setText("CUIN: " + cuin);
        } else {
            userName.setText("NAME: NOT LOGGED IN");
            userId.setText("ID: NOT LOGGED IN");
        }

        // **Handle Timetable Button Click**
        timetableButton.setOnClickListener(view -> {
            if (teacherPrefs.contains("teacher_name") && teacherPrefs.contains("teacher_id")) {
                // Open Teachers Timetable
                Intent intent = new Intent(Home_Page.this, TeachersTimetable.class);
                startActivity(intent);
            } else if (studentPrefs.contains("student_name") && studentPrefs.contains("student_CUIN")) {
                // Open Students Timetable
                Intent intent = new Intent(Home_Page.this, Students_Timetable.class);
                startActivity(intent);
            } else {
                // If no valid login, redirect to login page
                Intent intent = new Intent(Home_Page.this, MainLoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        // **Handle Attendance Button Click**
        attendanceButton.setOnClickListener(view -> {
            if (teacherPrefs.contains("teacher_name") && teacherPrefs.contains("teacher_id")) {
                Intent intent = new Intent(Home_Page.this, TeacherAttendance.class);
                startActivity(intent);
            } else if (studentPrefs.contains("student_name") && studentPrefs.contains("student_CUIN")) {
                Intent intent = new Intent(Home_Page.this, StudentAttendence.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Home_Page.this, MainLoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        // **Handle Logout Button Click**
        logoutButton.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", (dialog, which) -> logoutUser());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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
