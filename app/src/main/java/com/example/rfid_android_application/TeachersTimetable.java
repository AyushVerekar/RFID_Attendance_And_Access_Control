package com.example.rfid_android_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class TeachersTimetable extends AppCompatActivity {

    private TableLayout tableLayout;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "Teachers_Timetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityteachers_timetable);

        tableLayout = findViewById(R.id.TeacherTimetable);
        sharedPreferences = getSharedPreferences("TeacherPrefs", Context.MODE_PRIVATE);

        fetchTimetable();
    }

    private void fetchTimetable() {
        String teacherId = sharedPreferences.getString("teacher_id", "").trim();

        if (teacherId.isEmpty()) {
            Toast.makeText(this, "Error: Teacher ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.79.1/rfid/teacherTimetable.php?teacher_id=" + teacherId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "API Response: " + response.toString());

                        if (response.length() == 0) {
                            Toast.makeText(TeachersTimetable.this, "No timetable data found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            tableLayout.removeAllViews();

                            // Define time slots (including break)
                            String[] timeSlots = {"8:45", "9:45", "Break", "11:15", "12:15"};
                            Map<String, String[]> timetableData = new LinkedHashMap<>();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);
                                String day = row.getString("day");
                                String slot = row.getString("slot");
                                String subject = row.optString("subject_code", "-");

                                if (!timetableData.containsKey(day)) {
                                    timetableData.put(day, new String[timeSlots.length]);
                                }

                                for (int j = 0; j < timeSlots.length; j++) {
                                    if (timeSlots[j].equals(slot)) {
                                        timetableData.get(day)[j] = subject;
                                    }
                                }
                            }

                            // 🔹 Create Table Header Row (Day + Time Slots)
                            TableRow headerRow = new TableRow(TeachersTimetable.this);

                            // "Day" column header
                            TextView dayHeader = createHeaderTextView("Day");
                            headerRow.addView(dayHeader);

                            // Time slots column headers
                            for (String slot : timeSlots) {
                                TextView slotHeader = createHeaderTextView(slot);
                                headerRow.addView(slotHeader);
                            }

                            tableLayout.addView(headerRow);

                            // 🔹 Add Data Rows for each Day
                            for (Map.Entry<String, String[]> entry : timetableData.entrySet()) {
                                TableRow tableRow = new TableRow(TeachersTimetable.this);
                                TextView dayView = createDayTextView(entry.getKey());
                                tableRow.addView(dayView);

                                String[] subjects = entry.getValue();
                                for (int i = 0; i < timeSlots.length; i++) {
                                    TextView subjectView;
                                    if (timeSlots[i].equals("Break")) {
                                        subjectView = createBreakTextView();
                                    } else {
                                        subjectView = createSubjectTextView(subjects[i] == null ? "-" : subjects[i]);
                                    }
                                    tableRow.addView(subjectView);
                                }

                                tableLayout.addView(tableRow);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
            Log.e(TAG, "API Error: " + error.toString());
            Toast.makeText(TeachersTimetable.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }

    // 🔹 Utility Method for Creating Header TextView (Day & Time Slots)
    private TextView createHeaderTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.DKGRAY);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    // 🔹 Utility Method for Creating Day Name TextView
    private TextView createDayTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    // 🔹 Utility Method for Creating Subject Code TextView
    private TextView createSubjectTextView(String subject) {
        TextView textView = new TextView(this);
        textView.setText(subject != null ? subject : "-");  // Show "-" if empty
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    // 🔹 Utility Method for Creating Break TextView
    private TextView createBreakTextView() {
        TextView textView = new TextView(this);
        textView.setText("Break");
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
