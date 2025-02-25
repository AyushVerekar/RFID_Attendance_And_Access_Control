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
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Students_Timetable extends AppCompatActivity {

    private TableLayout tableLayout;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "Students_Timetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_timetable);

        tableLayout = findViewById(R.id.StudentTimetable);
        sharedPreferences = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);

        fetchTimetable();
    }

    private void fetchTimetable() {
        String studentCUIN = sharedPreferences.getString("student_CUIN", "").trim();

        if (studentCUIN.isEmpty()) {
            Toast.makeText(this, "Error: Student CUIN not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.79.1/rfid/studentTimetable.php?CUIN=" + studentCUIN;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "API Response: " + response.toString());

                    if (response.length() == 0) {
                        Toast.makeText(Students_Timetable.this, "No timetable data found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        tableLayout.removeAllViews();

                        String[] timeSlots = {"8:45", "9:45", "10:45", "11:15", "12:15"};
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
                        TableRow headerRow = new TableRow(Students_Timetable.this);
                        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.mainBlueColor));

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
                            TableRow tableRow = new TableRow(Students_Timetable.this);

                            // Add Day Name
                            TextView dayView = createDayTextView(entry.getKey());
                            tableRow.addView(dayView);

                            // Add Subject Codes
                            String[] subjects = entry.getValue();
                            for (int i = 0; i < timeSlots.length; i++) {
                                TextView subjectView;
                                if (timeSlots[i].equals("10:45")) {
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
                }, error -> {
            Log.e(TAG, "API Error: " + error.toString());
            Toast.makeText(Students_Timetable.this, "Error fetching data. Check API URL or network.", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }

    // 🔹 Create Header TextView
    private TextView createHeaderTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.mainBlueColor));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    // 🔹 Create Day TextView
    private TextView createDayTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    // 🔹 Create Subject Code TextView
    private TextView createSubjectTextView(String subject) {
        TextView textView = new TextView(this);
        textView.setText(subject != null ? subject : "-");
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    // 🔹 Create Break TextView
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
