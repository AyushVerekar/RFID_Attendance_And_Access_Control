package com.example.rfid_android_application;

public class AttendanceItem {
    private String details;
    private int percentage;

    public AttendanceItem(String details, int percentage) {
        this.details = details;
        this.percentage = percentage;
    }

    public String getDetails() {
        return details;
    }

    public int getPercentage() {
        return percentage;
    }
}
