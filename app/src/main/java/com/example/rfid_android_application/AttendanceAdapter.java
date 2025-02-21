package com.example.rfid_android_application;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AttendanceAdapter extends BaseAdapter {
    private Context context;
    private List<AttendanceItem> attendanceList;

    public AttendanceAdapter(Context context, List<AttendanceItem> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @Override
    public int getCount() {
        return attendanceList.size();
    }

    @Override
    public Object getItem(int position) {
        return attendanceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_attendance, parent, false);
        }

        AttendanceItem item = attendanceList.get(position);

        TextView textViewDetails = convertView.findViewById(R.id.textViewStudentDetails);
        TextView textViewAttendancePercentage = convertView.findViewById(R.id.textViewAttendancePercentage);

        // Set bold text
        textViewDetails.setText(item.getDetails());

        // Set attendance percentage and color
        textViewAttendancePercentage.setText(item.getPercentage() + "%");
        if (item.getPercentage() >= 75) {
            textViewAttendancePercentage.setTextColor(Color.GREEN);
        } else {
            textViewAttendancePercentage.setTextColor(Color.RED);
        }

        return convertView;
    }
}
