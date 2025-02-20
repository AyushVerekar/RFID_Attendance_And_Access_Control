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

public class LoginStudentPage extends AppCompatActivity {

    Button studentLogin;
    EditText studentName, studentCuin;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("student_name") && sharedPreferences.contains("student_CUIN")) {
            navigateToHome();
            return;
        }

        setContentView(R.layout.activity_login_student_page);

        studentLogin = findViewById(R.id.LoginStudentLoginSubmitButton);
        studentName = findViewById(R.id.LoginStudentNameText);
        studentCuin = findViewById(R.id.LoginStudentCUINText);

        studentLogin.setOnClickListener(v -> {
            String name = studentName.getText().toString().trim();
            String cuin = studentCuin.getText().toString().trim();

            if (!cuin.isEmpty() && !name.isEmpty()) {
                login(name, cuin);
            } else {
                Toast.makeText(LoginStudentPage.this, "Enter Name and CUIN", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String student_name, String CUIN) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.159.190/rfid/student.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "student_name=" + URLEncoder.encode(student_name, "UTF-8") +
                        "&CUIN=" + URLEncoder.encode(CUIN, "UTF-8");

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
                        saveLoginData(student_name, CUIN);
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

    private void saveLoginData(String student_name, String CUIN) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("student_name", student_name);
        editor.putString("student_CUIN", CUIN);
        editor.apply();
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginStudentPage.this, Home_Page.class);
        startActivity(intent);
        finish();
    }
}
