package com.example.rfid_android_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Home_Page extends AppCompatActivity {

    private Button logoutButton, timetableButton, attendanceButton, classworkButton;
    private SharedPreferences studentPrefs, teacherPrefs;
    private TextView userName, userId;
    private boolean isTeacher, isStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize UI elements
        logoutButton = findViewById(R.id.logoutButton);
        timetableButton = findViewById(R.id.HomeTimeTableButton);
        attendanceButton = findViewById(R.id.HomeAttendanceButton);
        classworkButton = findViewById(R.id.HomeClassworkButton);
        userName = findViewById(R.id.UserName);
        userId = findViewById(R.id.UserId);

        // Load SharedPreferences
        studentPrefs = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        teacherPrefs = getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);

        // Check if logged in as Teacher or Student
        isTeacher = teacherPrefs.contains("teacher_name") && teacherPrefs.contains("teacher_id");
        isStudent = studentPrefs.contains("student_name") && studentPrefs.contains("student_CUIN");

        // Set User Details
        String name = isTeacher ? teacherPrefs.getString("teacher_name", "Unknown Teacher")
                : studentPrefs.getString("student_name", "Unknown Student");
        String id = isTeacher ? teacherPrefs.getString("teacher_id", "Unknown ID")
                : studentPrefs.getString("student_CUIN", "Unknown CUIN");

        userName.setText("NAME: " + name.toUpperCase());
        userId.setText(isTeacher ? "ID: " + id : "CUIN: " + id);

        // If no valid login, show "Not Logged In"
        if (!isTeacher && !isStudent) {
            userName.setText("NAME: NOT LOGGED IN");
            userId.setText("ID: NOT LOGGED IN");
        }

        // **Handle Timetable Button Click**
        timetableButton.setOnClickListener(view -> openActivity(
                isTeacher ? TeachersTimetable.class : isStudent ? Students_Timetable.class : MainLoginPage.class));

        // **Handle Attendance Button Click**
        attendanceButton.setOnClickListener(view -> openActivity(
                isTeacher ? TeacherAttendance.class : isStudent ? StudentAttendence.class : MainLoginPage.class));

        // **Handle Classwork Button Click**
        classworkButton.setOnClickListener(view -> openActivity(ActivityClasswork.class));

        // **Show Classwork button only if logged in**
        classworkButton.setVisibility((isTeacher || isStudent) ? View.VISIBLE : View.GONE);

        // **Handle Logout Button Click**
        logoutButton.setOnClickListener(v -> showLogoutDialog());
    }

    // **Reusable Method to Open Activities**
    private void openActivity(Class<?> activity) {
        startActivity(new Intent(Home_Page.this, activity));
    }

    // **Show Logout Confirmation Dialog**
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // **Perform Logout Operation**
    private void logoutUser() {
        studentPrefs.edit().clear().apply();
        teacherPrefs.edit().clear().apply();

        openActivity(MainLoginPage.class);
        finish();
    }
}
