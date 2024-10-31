package com.example.musicapp.MusicManager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MusicDetailActivity extends AppCompatActivity {

    TextView tvTime, tvTitle, tvArtist, tvDuration, tvSinger;
    Button editBtn, deleteBtn;
    ImageView tvImage;
    SeekBar seekBarTime, seekBarVolume;
    MediaPlayer mMediaPlayer;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ref;
    int duration;
    String key, nameSong, artist, singer, mp3Url, imageUrl, oldAudioUrl;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_music_detail);
            initializeViews();
            loadSongDetails();
            setupSeekBars();
            setupButtons();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error ", e);
            Toast.makeText(this, "Error initializing activity", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.manageTitle);
        tvArtist = findViewById(R.id.manageArtist);
        tvDuration = findViewById(R.id.manageDuration);
        tvSinger = findViewById(R.id.manageSinger);
        tvTime = findViewById(R.id.manageTime);
        tvImage = findViewById(R.id.manageImage);
        deleteBtn = findViewById(R.id.deleteMusic);
        editBtn = findViewById(R.id.editMusic);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
    }

    private void loadSongDetails() {
        try {
            Intent intent = getIntent();
            if (intent == null) {
                Log.e(TAG, "loadSongDetails: Intent is null");
                return;
            }

            Log.d(TAG, "loadSongDetails: Getting extras from intent");

            // Lấy và log tất cả các extra
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Log.d(TAG, "Extra " + key + " = " + extras.get(key));
                }
            }

            nameSong = intent.getStringExtra("NameSong");
            artist = intent.getStringExtra("Artist");
            singer = intent.getStringExtra("Singer");
            mp3Url = intent.getStringExtra("MP3");
            imageUrl = intent.getStringExtra("Image");
            key = intent.getStringExtra("Key");
            // Update UI
            updateUI();

        } catch (Exception e) {
            Log.e(TAG, "loadSongDetails: Error ", e);
            Toast.makeText(this, "Error loading song details", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        try {
            if (tvTitle != null) tvTitle.setText(nameSong != null ? nameSong : "");
            if (tvArtist != null) tvArtist.setText(artist != null ? artist : "");
            if (tvSinger != null) tvSinger.setText(singer != null ? singer : "");

            if (imageUrl != null && !imageUrl.isEmpty() && tvImage != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .error(R.drawable.img) // Thêm ảnh mặc định
                        .into(tvImage);
            }

            if (mp3Url != null && !mp3Url.isEmpty()) {
                initializeMediaPlayer();
            }
        } catch (Exception e) {
            Log.e(TAG, "updateUI: Error ", e);
            Toast.makeText(this, "Error updating UI", Toast.LENGTH_LONG).show();
        }
    }
    private void initializeMediaPlayer() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }

            Uri uri = Uri.parse(mp3Url);
            mMediaPlayer = MediaPlayer.create(this, uri);

            if (mMediaPlayer == null) {
                Log.e(TAG, "initializeMediaPlayer: Failed to create MediaPlayer");
                return;
            }

            mMediaPlayer.setOnPreparedListener(mp -> {
                try {
                    seekBarTime.setMax(mMediaPlayer.getDuration());
                    tvDuration.setText(millisecondsToString(mMediaPlayer.getDuration()));
                    mMediaPlayer.start();
                    updateSeekBar();
                } catch (Exception e) {
                    Log.e(TAG, "MediaPlayer onPrepared: Error ", e);
                }
            });

            mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer Error: " + what + ", " + extra);
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "initializeMediaPlayer: Error ", e);
            Toast.makeText(this, "Error initializing media player", Toast.LENGTH_LONG).show();
        }
    }
    private void setupMediaPlayer(String mp3Url) {
        Uri uri = Uri.parse(mp3Url);
        mMediaPlayer = MediaPlayer.create(this, uri);
        mMediaPlayer.setOnPreparedListener(mp -> {
            seekBarTime.setMax(mMediaPlayer.getDuration());
            tvDuration.setText(millisecondsToString(mMediaPlayer.getDuration()));
            mMediaPlayer.start();
            updateSeekBar();
        });
    }

    private void setupSeekBars() {
        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                mMediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    tvTime.setText(millisecondsToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    seekBarTime.setProgress(currentPosition);
                    tvTime.setText(millisecondsToString(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }

    private void setupButtons() {
        try {
            if (editBtn == null || deleteBtn == null) {
                Log.e(TAG, "setupButtons: Buttons not initialized");
                return;
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleEditButtonClick();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "setupButtons: Error ", e);
        }
    }

    private void handleEditButtonClick() {
        try {
            Log.d(TAG, "handleEditButtonClick: Button clicked");

            if (!validateData()) {
                return;
            }

            Intent intent = new Intent(MusicDetailActivity.this, UpdateActivity.class);
            // Ensure all required data is passed with consistent keys
            intent.putExtra("Key", key);
            intent.putExtra("NameSong", nameSong);
            intent.putExtra("Artist", artist);
            intent.putExtra("Singer", singer);
            intent.putExtra("MP3", mp3Url);
            intent.putExtra("Image", imageUrl);
            intent.putExtra("Duration", mMediaPlayer != null ? mMediaPlayer.getDuration() : 0);

            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "handleEditButtonClick: Error ", e);
            Toast.makeText(this, "Error opening update screen", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateData() {
        if (key == null || nameSong == null || artist == null ||
                singer == null || mp3Url == null || imageUrl == null) {
            Log.e(TAG, "validateData: Missing required data");
            Toast.makeText(this, "Error: Missing data", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Song")
                .setMessage("Are you sure you want to delete this song?")
                .setPositiveButton("Yes", (dialog, which) -> deleteSong())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteSong() {
        db.collection("Songs").document(key)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MusicDetailActivity.this, "Song deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MusicDetailActivity.this, MusicManagerActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(MusicDetailActivity.this, "Error deleting song", Toast.LENGTH_SHORT).show());
    }
    private static final String TAG = "MusicDetailActivity";

    private String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            handler.removeCallbacksAndMessages(null);
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: Error ", e);
            super.onDestroy();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshSongData();
    }
    private void refreshSongData() {
        if (key != null && !key.isEmpty()) {
            db.collection("Songs").document(key).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            nameSong = documentSnapshot.getString("NameSong");
                            artist = documentSnapshot.getString("Artist");
                            singer = documentSnapshot.getString("Singer");
                            mp3Url = documentSnapshot.getString("MP3");
                            imageUrl = documentSnapshot.getString("Image");

                            // Update UI with refreshed data
                            tvTitle.setText(nameSong != null ? nameSong : "");
                            tvArtist.setText(artist != null ? artist : "");
                            tvSinger.setText(singer != null ? singer : "");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this).load(imageUrl).into(tvImage);
                            }

                            // Reinitialize MediaPlayer if audio URL has changed
                            if (!mp3Url.equals(oldAudioUrl)) {
                                if (mMediaPlayer != null) {
                                    mMediaPlayer.release();
                                }
                                setupMediaPlayer(mp3Url);
                                oldAudioUrl = mp3Url;
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error refreshing song data", Toast.LENGTH_SHORT).show());
        }
        }
}