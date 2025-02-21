package com.example.rfid_android_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TeacherAttendance extends AppCompatActivity {
    private static final String TAG = "TeacherAttendance";
    ListView attendanceListView;
    SharedPreferences sharedPreferences;
    String teacherId;
    List<AttendanceItem> studentAttendanceList;
    AttendanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        attendanceListView = findViewById(R.id.attendanceListView);
        studentAttendanceList = new ArrayList<>();
        adapter = new AttendanceAdapter(this, studentAttendanceList);
        attendanceListView.setAdapter(adapter);

        sharedPreferences = getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);
        teacherId = sharedPreferences.getString("teacher_id", "");

        if (teacherId.isEmpty()) {
            Toast.makeText(this, "Teacher ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchAttendance();
    }

    private void fetchAttendance() {
        new Thread(() -> {
            try {
                String urlString = "http://192.168.159.245/rfid/teacherAttendance.php?Tr_id=" + teacherId;
                Log.d(TAG, "Requesting URL: " + urlString); // Debugging log

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int responseCode = conn.getResponseCode();

                Log.d(TAG, "Response Code: " + responseCode); // Log HTTP response code

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d(TAG, "Server Response: " + response.toString()); // Log raw response

                    JSONArray attendanceArray = new JSONArray(response.toString());
                    studentAttendanceList.clear();

                    for (int i = 0; i < attendanceArray.length(); i++) {
                        JSONObject studentData = attendanceArray.getJSONObject(i);

                        String name = studentData.getString("Student_name");
                        String subjectCode = studentData.getString("Subject_code");
                        int attendedLectures = studentData.getInt("Attended_lectures");
                        int totalLectures = studentData.getInt("Total_lectures");

                        float attendancePercentage = (totalLectures > 0) ? ((attendedLectures * 100f) / totalLectures) : 0;

                        String displayText = name + " - " + subjectCode + "\n" +
                                "Attended: " + attendedLectures + "/" + totalLectures;

                        studentAttendanceList.add(new AttendanceItem(displayText, Math.round(attendancePercentage)));
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                } else {
                    Log.e(TAG, "HTTP Error: " + responseCode);
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Server Error: " + responseCode, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception in fetchAttendance()", e);
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error fetching attendance.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
