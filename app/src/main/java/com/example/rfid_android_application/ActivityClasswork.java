package com.example.rfid_android_application;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.*;

public class ActivityClasswork extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
    private Button uploadButton;
    private LinearLayout fileListLayout;
    private ProgressBar progressBar;
    private SharedPreferences teacherPrefs,studentPrefs;

    private static final String BASE_URL = "http://192.168.217.1/rfid/";
    private static final String UPLOAD_URL = BASE_URL + "upload.php";
    private static final String FETCH_URL = BASE_URL + "fetch_files.php";
    private static final String DELETE_URL = BASE_URL + "delete_file.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classwork);

        uploadButton = findViewById(R.id.uploadButton);
        fileListLayout = findViewById(R.id.fileListLayout);
        progressBar = findViewById(R.id.progressBar);
        teacherPrefs = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);
        studentPrefs = getSharedPreferences("StudentPrefs", MODE_PRIVATE);




        if (teacherPrefs.contains("teacher_id")) { // Updated to use teacher_id
            uploadButton.setVisibility(View.VISIBLE);
            uploadButton.setOnClickListener(view -> openFileChooser());
        } else {
            uploadButton.setVisibility(View.GONE);
        }

        fetchUploadedFiles();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                uploadFile(selectedFileUri, getFileName(selectedFileUri));
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "unknown.pdf";
        try {
            String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
            try (android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private void uploadFile(Uri fileUri, String fileName) {
        progressBar.setVisibility(View.VISIBLE);

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            File tempFile = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            String teacherId = teacherPrefs.getString("teacher_id", "0"); // Fetch teacher_id

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, RequestBody.create(tempFile, MediaType.parse("application/pdf")))
                    .addFormDataPart("teacher_id", teacherId) // Updated field
                    .build();

            Request request = new Request.Builder().url(UPLOAD_URL).post(requestBody).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ActivityClasswork.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ActivityClasswork.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        fetchUploadedFiles();
                    });
                }
            });

        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void fetchUploadedFiles() {
        fileListLayout.removeAllViews();

        String urlWithParams = FETCH_URL;  // Default URL

        if (teacherPrefs.contains("teacher_id")) {  // If logged in as teacher
            String teacherId = teacherPrefs.getString("teacher_id", "0");
            Log.d("Classwork", "Teacher logged in, fetching files uploaded by teacher ID: " + teacherId);
            urlWithParams += "?teacher_id=" + teacherId;
        } else if (studentPrefs.contains("student_CUIN")) {  // If logged in as student
            String cuin = studentPrefs.getString("student_CUIN", "0");
            Log.d("Classwork", "Student logged in, fetching files for CUIN: " + cuin);
            urlWithParams += "?cuin=" + cuin;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urlWithParams).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ActivityClasswork.this, "Failed to Fetch Files", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            JSONArray jsonArray = jsonResponse.getJSONArray("files");

                            if (jsonArray.length() == 0) {
                                Toast.makeText(ActivityClasswork.this, "No files available", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject fileObject = jsonArray.getJSONObject(i);
                                String fileName = fileObject.getString("name");
                                String teacherName = fileObject.getString("teacher_name");

                                View fileView = getLayoutInflater().inflate(R.layout.file_item, fileListLayout, false);
                                TextView fileNameTextView = fileView.findViewById(R.id.fileName);
                                Button downloadButton = fileView.findViewById(R.id.downloadButton);
                                Button deleteButton = fileView.findViewById(R.id.deleteButton);

                                fileNameTextView.setText(fileName + " (Uploaded by: " + teacherName + ")");

                                downloadButton.setOnClickListener(v -> downloadFile(fileName));

                                if (teacherPrefs.contains("teacher_id")) {  // Show delete button for teachers
                                    deleteButton.setVisibility(View.VISIBLE);
                                    deleteButton.setOnClickListener(v -> confirmDelete(fileName));
                                } else {
                                    deleteButton.setVisibility(View.GONE);
                                }

                                fileListLayout.addView(fileView);
                            }
                        } else {
                            Toast.makeText(ActivityClasswork.this, "No files found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }






    private void confirmDelete(String fileName) {
        new AlertDialog.Builder(this)
                .setTitle("Delete File")
                .setMessage("Are you sure you want to delete this file?")
                .setPositiveButton("Yes", (dialog, which) -> deleteFileFromServer(fileName))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteFileFromServer(String fileName) {
        OkHttpClient client = new OkHttpClient();
        String teacherId = teacherPrefs.getString("teacher_id", "0"); // Fetch teacher_id
        RequestBody requestBody = new FormBody.Builder()
                .add("file_name", fileName.trim())
                .add("teacher_id", teacherId) // Send teacher_id
                .build();

        Request request = new Request.Builder()
                .url(DELETE_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ActivityClasswork.this, "Failed to delete", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.getString("message");

                    runOnUiThread(() -> {
                        Toast.makeText(ActivityClasswork.this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            fetchUploadedFiles();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void downloadFile(String fileName) {
        String fileUrl = BASE_URL + "uploads/" + fileName;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            Toast.makeText(this, "Download Started", Toast.LENGTH_SHORT).show();
        }
    }
}