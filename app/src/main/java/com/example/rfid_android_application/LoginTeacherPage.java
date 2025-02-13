package com.example.rfid_android_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginTeacherPage extends AppCompatActivity {

    Button teacherLogin;
    EditText teacherName, teacherId;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("teacher_name") && sharedPreferences.contains("teacher_id")) {
            navigateToHome();
            return;
        }

        setContentView(R.layout.activity_login_teacher_page);

        teacherLogin = findViewById(R.id.LoginTeacherLoginSubmitButton);
        teacherName = findViewById(R.id.LoginTeacherNameText);
        teacherId = findViewById(R.id.LoginTeacherIDText);

        teacherLogin.setOnClickListener(v -> {
            String name = teacherName.getText().toString().trim();
            String id = teacherId.getText().toString().trim();

            if (!name.isEmpty() && !id.isEmpty()) {
                login(name, id);
            } else {
                Toast.makeText(LoginTeacherPage.this, "Enter Name and ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String teacher_name, String teacher_id) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.159.245/sheared/teacherlogin.php"); // Change to your actual PHP URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "Tr_name=" + URLEncoder.encode(teacher_name, "UTF-8") +
                        "&Tr_id=" + URLEncoder.encode(teacher_id, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                reader.close();

                runOnUiThread(() -> {
                    if ("Login successful".equals(response)) {
                        Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                        saveLoginData(teacher_name, teacher_id);
                        navigateToHome();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Login Failed. Try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void saveLoginData(String teacher_name, String teacher_id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("teacher_name", teacher_name);
        editor.putString("teacher_id", teacher_id);
        editor.apply();
    }


    private void navigateToHome() {
        Intent intent = new Intent(LoginTeacherPage.this, Home_Page.class);
        startActivity(intent);
        finish();
    }
}
