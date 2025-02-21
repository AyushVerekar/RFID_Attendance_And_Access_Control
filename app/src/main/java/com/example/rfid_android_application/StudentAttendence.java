package com.example.rfid_android_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StudentAttendence extends AppCompatActivity {
    private static final String TAG = "StudentsAttendance";

    TextView totalLecturesNo, lecturesAttendedNo, attendancePercentageNo;
    Button btnRefresh;
    SharedPreferences sharedPreferences;
    String CUIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendence);

        // Initialize UI elements
        totalLecturesNo = findViewById(R.id.totalLecturesNo);
        lecturesAttendedNo = findViewById(R.id.lecturesAttendedNo);
        attendancePercentageNo = findViewById(R.id.attendancePercentageNo);
        btnRefresh = findViewById(R.id.btnRefresh);

        // Get CUIN from SharedPreferences
        sharedPreferences = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        CUIN = sharedPreferences.getString("student_CUIN", "");

        if (CUIN.isEmpty()) {
            Toast.makeText(this, "CUIN not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch attendance data when the activity starts
        fetchAttendance();

        // Set refresh button click listener
        btnRefresh.setOnClickListener(v -> fetchAttendance());
    }

    private void fetchAttendance() {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.159.245/rfid/studentAttendance.php?CUIN=" + CUIN);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Log response for debugging
                Log.d(TAG, "Server Response: " + response.toString());

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.getString("status").equals("success")) {
                    JSONArray attendanceArray = jsonResponse.getJSONArray("attendance");

                    int totalLectures = 0;
                    int attendedLectures = 0;

                    // Loop through all subjects and calculate total
                    for (int i = 0; i < attendanceArray.length(); i++) {
                        JSONObject subjectData = attendanceArray.getJSONObject(i);
                        totalLectures += subjectData.getInt("total_lectures");
                        attendedLectures += subjectData.getInt("attended_lectures");
                    }

                    // Calculate attendance percentage
                    int attendancePercentage = (totalLectures == 0) ? 0 : attendedLectures *100 / totalLectures;

                    // Update UI on the main thread
                    int finalTotalLectures = totalLectures;
                    int finalAttendedLectures = attendedLectures;
                    int finalAttendancePercentage = attendancePercentage;
                    runOnUiThread(() -> {
                        totalLecturesNo.setText(String.valueOf(finalTotalLectures));
                        lecturesAttendedNo.setText(String.valueOf(finalAttendedLectures));
                        attendancePercentageNo.setText(finalAttendancePercentage + "%");
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "No attendance found.", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error fetching attendance. Please try again.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}