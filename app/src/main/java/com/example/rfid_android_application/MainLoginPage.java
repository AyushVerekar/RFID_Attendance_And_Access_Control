package com.example.rfid_android_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainLoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_page);


        Button studentLoginButton;
        Button teacherLoginButton;

        studentLoginButton=findViewById(R.id.MainLoginPageStudentButton);
        teacherLoginButton=findViewById(R.id.MainLoginPageTeacherButton);

        studentLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLoginPage.this,LoginStudentPage.class);
                startActivity(intent);
            }
        });

        teacherLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLoginPage.this,LoginTeacherPage.class);
                startActivity(intent);
            }
        });
    }
}