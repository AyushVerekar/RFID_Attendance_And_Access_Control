package com.example.rfid_android_application;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

public class CsTeachersTimetable extends AppCompatActivity {

    private TableLayout tableLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cs_teachers_timetable);

        tableLayout = findViewById(R.id.CsTeachersTimetable);

        fetchTimetable();
    }

    private void fetchTimetable() {
        String url = "http://192.168.63.245/timetable2/timetable.php";

        RequestQueue queue = Volley.newRequestQueue(this);


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);

                                TableRow tableRow = new TableRow(CsTeachersTimetable.this);

                                TextView dayView = new TextView(CsTeachersTimetable.this);
                                dayView.setText(row.getString("day"));
                                dayView.setPadding(12, 12, 12, 12);
                                tableRow.addView(dayView);
                                dayView.setTextColor(Color.WHITE);
                                dayView.setTextSize(20);
                                dayView.setBackgroundResource(R.drawable.timetablebackground);


                                String[] timeSlots = {
                                        "8:45 - 9:45", "9:45 - 10:45", "10:45 - 11:15",
                                        "11:15 - 12:15", "12:15 - 1:15", "1:15 - 2:15", "2:15 - 3:15"

                                };

                                for (String timeSlot : timeSlots) {
                                    TextView subjectView = new TextView(CsTeachersTimetable.this);
                                    String subject = row.optString(timeSlot, "No Subject");
                                    subjectView.setText(subject);
                                    subjectView.setPadding(12, 12, 12, 12);
                                    subjectView.setTextSize(20);
                                    subjectView.setBackgroundResource(R.drawable.timetablebackground);
                                    subjectView
                                            .setTextColor(Color.WHITE);
                                    tableRow.addView(subjectView);
                                }


                                tableLayout.addView(tableRow);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CsTeachersTimetable.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CsTeachersTimetable.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                        Log.e("Volley", error.toString());
                    }
                });

        queue.add(request);
    }
}
