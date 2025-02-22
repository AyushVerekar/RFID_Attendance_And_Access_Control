package com.example.rfid_android_application;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
    private SharedPreferences teacherPrefs;
    private static final String UPLOAD_URL = "http://192.168.159.245/rfid/upload.php";
    private static final String FETCH_URL = "http://192.168.159.245/rfid/fetch_files.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classwork);

        uploadButton = findViewById(R.id.uploadButton);
        fileListLayout = findViewById(R.id.fileListLayout);
        progressBar = findViewById(R.id.progressBar);
        teacherPrefs = getSharedPreferences("TeacherPrefs", MODE_PRIVATE);

        if (teacherPrefs.contains("teacher_name")) {
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

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, RequestBody.create(tempFile, MediaType.parse("application/pdf")))
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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(FETCH_URL).get().build();

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
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject fileObject = jsonArray.getJSONObject(i);
                            String fileName = fileObject.getString("name");
                            String fileUrl = "http://192.168.159.245/rfid/" + fileObject.getString("url");

                            TextView fileTextView = new TextView(ActivityClasswork.this);
                            fileTextView.setText(fileName);
                            fileTextView.setTextSize(18);
                            fileTextView.setPadding(10, 10, 10, 10);
                            fileTextView.setOnClickListener(v -> downloadFile(fileName, fileUrl));

                            fileListLayout.addView(fileTextView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void downloadFile(String fileName, String fileUrl) {
        Toast.makeText(this, "Downloading " + fileName, Toast.LENGTH_SHORT).show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setTitle(fileName);
        request.setDescription("Downloading classwork...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            Toast.makeText(this, "Download Manager not available", Toast.LENGTH_SHORT).show();
        }
    }
}