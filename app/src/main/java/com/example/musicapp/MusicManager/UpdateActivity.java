package com.example.musicapp.MusicManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.bumptech.glide.Glide;
import com.example.musicapp.Class.Song;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateActivity extends AppCompatActivity {
    private ImageView updateImage;
    private EditText updateName, updateArtist, updateSinger, editTextImageUrl;
    private Button saveButton, audioSave, btnLoadImage;
    private String key, oldImageUrl, oldAudioUrl, imageUrl, audioUrl;
    private int duration;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        // Khởi tạo các view
        initializeViews();

        // Nhận dữ liệu từ Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("Key");
            if (key == null || key.isEmpty()) {
                Toast.makeText(this, "Lỗi: Không tìm thấy khóa hợp lệ", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            loadExistingData(bundle);
        } else {
            Toast.makeText(this, "Lỗi: Không có dữ liệu", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Cài đặt các sự kiện click
        setupClickListeners();
    }

    private void initializeViews() {
        updateImage = findViewById(R.id.updateImage);
        saveButton = findViewById(R.id.saveUpdateButton);
        //audioSave = findViewById(R.id.btnUpdateFile);
        updateName = findViewById(R.id.updateName);
        updateArtist = findViewById(R.id.updateArtist);
        updateSinger = findViewById(R.id.updateSinger);
        editTextImageUrl = findViewById(R.id.editTextImageUrl);
        btnLoadImage = findViewById(R.id.btnLoadImage);
    }


    private void loadExistingData(Bundle bundle) {
        Glide.with(UpdateActivity.this)
                .load(bundle.getString("Image"))
                .error(R.drawable.img)
                .into(updateImage);

        updateName.setText(bundle.getString("NameSong"));
        updateArtist.setText(bundle.getString("Artist"));
        updateSinger.setText(bundle.getString("Singer"));

        oldImageUrl = bundle.getString("Image");
        oldAudioUrl = bundle.getString("MP3");
        duration = bundle.getInt("Duration", 0);

        imageUrl = oldImageUrl;
        audioUrl = oldAudioUrl;
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> UpdateData());
        btnLoadImage.setOnClickListener(v -> loadImageFromUrl());
    }

    private void loadImageFromUrl() {
        String imageUrlInput = editTextImageUrl.getText().toString().trim();
        if (!TextUtils.isEmpty(imageUrlInput)) {
            Glide.with(this)
                    .load(imageUrlInput)
                    .error(R.drawable.img) // Đặt ảnh mặc định nếu tải thất bại
                    .into(updateImage);
            imageUrl = imageUrlInput; // Lưu URL để cập nhật vào cơ sở dữ liệu
        } else {
            Toast.makeText(this, "Vui lòng nhập URL hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }


    private void UpdateData() {
        if (key == null || key.isEmpty()) {
            Toast.makeText(UpdateActivity.this, "Lỗi: Khóa tài liệu không hợp lệ", Toast.LENGTH_LONG).show();
            return;
        }

        String nameUpdate = updateName.getText().toString().trim();
        String artistUpdate = updateArtist.getText().toString().trim();
        String singerUpdate = updateSinger.getText().toString().trim();

        Map<String, Object> songData = new HashMap<>();
        songData.put("NameSong", nameUpdate);
        songData.put("Singer", singerUpdate);
        songData.put("MP3", audioUrl);
        songData.put("Duration", duration);
        songData.put("Image", imageUrl);
        songData.put("Artist", artistUpdate);
        songData.put("Key", key);

        AlertDialog progressDialog = showProgressDialog();

        db.collection("Songs")
                .document(key)
                .set(songData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    navigateToMusicManager();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateActivity.this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(UpdateActivity.this, MusicManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
