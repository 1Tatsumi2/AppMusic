package com.example.musicapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.MusicManager.MusicManagerActivity;
import com.example.musicapp.MusicManager.UpdateActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {
    private ImageView uploadImage;
    private SeekBar seekBarTime;
    private VideoView uploadVideo;
    private EditText uploadName, uploadArtist, uploadSinger, uploadTextImageUrl, uploadMp3, uploadMP4;
    private Button saveButton, audioSave, btnLoadImage, btnLoadMP3, btnLoadVideo;
    private String key, oldImageUrl, oldAudioUrl, imageUrl, audioUrl, MP4Url;
    private int duration;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initializeViews();

        setupClickListeners();
        uploadVideo.setVideoURI( Uri.parse( "android.resource://" + getPackageName() + "/" + uploadMP4 ) );
    }
    private void initializeViews() {
        uploadImage = findViewById(R.id.uploadImage);
        saveButton = findViewById(R.id.saveButton);
        //audioSave = findViewById(R.id.btnUpdateFile);
        uploadName = findViewById(R.id.uploadName);
        // Change this line in initializeViews()
        uploadArtist = findViewById(R.id.uploadArtist);
        uploadSinger = findViewById(R.id.uploadSinger);
        uploadTextImageUrl = findViewById(R.id.uploadTextImageUrl);
        btnLoadImage = findViewById(R.id.btnLoadImage);
        uploadMp3 = findViewById( R.id.uploadMp3 );
        uploadMP4 = findViewById( R.id.uploadMP4 );
        btnLoadMP3 = findViewById( R.id.btnLoadMP3 );
        btnLoadVideo = findViewById( R.id.btnLoadVideo );
        seekBarTime = findViewById( R.id.seekBarTime );
        uploadVideo = findViewById( R.id.uploadVideo );
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> UpdateData());
        btnLoadImage.setOnClickListener(v -> loadImageFromUrl());
        btnLoadVideo.setOnClickListener( v -> loadMP4FromUrl() );
    }

    private void loadImageFromUrl() {
        String imageUrlInput = uploadTextImageUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(imageUrlInput)) {
            Glide.with(this)
                    .load(imageUrlInput)
                    .error(R.drawable.img) // Đặt ảnh mặc định nếu tải thất bại
                    .into(uploadImage);
            imageUrl = imageUrlInput; // Lưu URL để cập nhật vào cơ sở dữ liệu

        } else {
            Toast.makeText(this, "Vui lòng nhập URL hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMP4FromUrl() {
        String MP4UrlInput = uploadMP4.getText().toString().trim();
        if (!TextUtils.isEmpty(MP4UrlInput)) {
            Glide.with(this)
                    .load(MP4UrlInput)
                    .error( Uri.parse( "android.resource://" + getPackageName() + "/" + R.raw.video2 ) ) ;// Đặt ảnh mặc định nếu tải thất bại

            MP4Url = MP4UrlInput; // Lưu URL để cập nhật vào cơ sở dữ liệu

        } else {
            Toast.makeText(this, "Vui lòng nhập URL hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }


    private void UpdateData() {
        if (key == null || key.isEmpty()) {
            Toast.makeText( UploadActivity.this, "Lỗi: Khóa tài liệu không hợp lệ", Toast.LENGTH_LONG).show();
            return;
        }

        String nameUpload = uploadName.getText().toString().trim();
        String artistUpload = uploadArtist.getText().toString().trim();
        String singerUpload = uploadSinger.getText().toString().trim();

        Map<String, Object> songData = new HashMap<>();
        songData.put("NameSong", nameUpload);
        songData.put("Singer", singerUpload);
        songData.put("MP3", audioUrl);
        songData.put("Duration", duration);
        songData.put("Image", imageUrl);
        songData.put("Artist", artistUpload);
        songData.put("Key", key);

        AlertDialog progressDialog = showProgressDialog();

        db.collection("Songs")
                .document(key)
                .set(songData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    navigateToMusicManager();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UpdateActivity", "Lỗi khi cập nhật tài liệu", e);
                });
    }
    private AlertDialog showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void navigateToMusicManager() {
        Intent intent = new Intent(UploadActivity.this, MusicManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
