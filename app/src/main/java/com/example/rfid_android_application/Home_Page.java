package com.example.rfid_android_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Home_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        Button csStudentsTimetable;
        csStudentsTimetable = findViewById(R.id.HomeTimeTableButton);

        csStudentsTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home_Page.this, StudentTimetable.class);
                startActivity(intent);
            }
        });
    }
}