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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class TeachersTimetable extends AppCompatActivity {

    private TableLayout tableLayout;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "Teacher_Timetable";

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

        String url = "http://192.168.217.1/rfid/teacherTimetable.php?Tr_id=" + teacherId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "API Response: " + response.toString());

                    if (response.length() == 0) {
                        Toast.makeText(TeachersTimetable.this, "No timetable data found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        tableLayout.removeViews(1, Math.max(0, tableLayout.getChildCount() - 1));

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject row = response.getJSONObject(i);
                            String day = row.optString("day", "Unknown");

                            String[] subjects = new String[]{"-", "-", "Break", "-", "-"};

                            for (int j = 1; j <= 5; j++) {
                                if (j == 3) continue;
                                String key = "slot_" + j;
                                int index = j > 3 ? j - 1 : j - 1;
                                subjects[index] = row.optString(key, "-");
                            }

                            TableRow tableRow = new TableRow(TeachersTimetable.this);

                            TextView dayView = createDayTextView(day);
                            tableRow.addView(dayView);

                            for (String subject : subjects) {
                                TextView subjectView = createSubjectTextView(subject);
                                tableRow.addView(subjectView);
                            }

                            tableLayout.addView(tableRow);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(TeachersTimetable.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e(TAG, "API Error: " + error.toString());
            Toast.makeText(TeachersTimetable.this, "Error fetching data. Check API URL or network.", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }

    private TextView createDayTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    private TextView createSubjectTextView(String subject) {
        TextView textView = new TextView(this);
        textView.setText(subject);
        textView.setPadding(12, 12, 12, 12);
        textView.setTextSize(18);
        textView.setTextColor(subject.equals("Break") ? Color.RED : Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
