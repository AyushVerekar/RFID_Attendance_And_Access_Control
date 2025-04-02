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

public class Students_Timetable extends AppCompatActivity {

    private TableLayout tableLayout;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "Students_Timetable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_timetable);

        tableLayout = findViewById(R.id.TeacherTimetable);
        sharedPreferences = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);

        fetchTimetable();
    }

    private void fetchTimetable() {
        String studentCUIN = sharedPreferences.getString("student_CUIN", "").trim();

        if (studentCUIN.isEmpty()) {
            Toast.makeText(this, "Error: Student CUIN not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.217.1/rfid/studentTimetable.php?CUIN=" + studentCUIN;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "API Response: " + response.toString());

                    if (response.length() == 0) {
                        Toast.makeText(Students_Timetable.this, "No timetable data found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        // Clear previous rows, keep header row
                        tableLayout.removeViews(1, Math.max(0, tableLayout.getChildCount() - 1));

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject row = response.getJSONObject(i);
                            String day = row.optString("day", "Unknown");

                            // Initialize subjects array with default values
                            String[] subjects = new String[]{"-", "-", "Break", "-", "-"};

                            for (int j = 1; j <= 5; j++) {
                                if (j == 3) continue; // Skip break slot
                                String key = "slot_" + j;
                                int index = j > 3 ? j - 1 : j - 1; // Adjust index for break
                                subjects[index] = row.optString(key, "-");
                            }

                            TableRow tableRow = new TableRow(Students_Timetable.this);

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
                        Toast.makeText(Students_Timetable.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            Log.e(TAG, "API Error: " + error.toString());
            Toast.makeText(Students_Timetable.this, "Error fetching data. Check API URL or network.", Toast.LENGTH_SHORT).show();
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
