package com.example.musicapp.Activities;

import static com.google.android.gms.common.ConnectionResult.TIMEOUT;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapp.Class.Song;
import com.example.musicapp.Interface.ActionPlaying;
import com.example.musicapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicPlayer extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    private Song currentSong;
    private ArrayList<Song> musicList;
    private int position;
    private MediaPlayer mMediaPlayer;

    private TextView tvTitle, tvArtist, tvDuration, tvTime;
    private SeekBar seekBarTime, seekBarVolume;
    private ImageView tvImage, shuffleBtn, loopBtn, btnBack;
    private Button btnPlay, nextBtn, previousBtn, back;
    private Handler handler;
    private boolean isLoopOn = false;  // Thêm biến kiểm tra lặp
    private boolean isShuffleOn = false;  // Thêm biến kiểm tra xáo trộn
    private ObjectAnimator rotateAnimator;
    private ArrayList<String> songIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Khởi tạo các view
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvDuration = findViewById(R.id.tvDuration);
        tvImage = findViewById(R.id.tvImage);
        btnBack = findViewById(R.id.btnBack);
        // Khởi tạo các view
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        tvTime = findViewById(R.id.tvTime);

        handler = new Handler();
        btnPlay = findViewById(R.id.btnPlay);
        shuffleBtn = findViewById(R.id.shuffleBtn);
        loopBtn = findViewById(R.id.LoopBtn);

        // Thiết lập listener cho các nút
        btnPlay.setOnClickListener(v -> playClicked());
        shuffleBtn.setOnClickListener(v -> toggleShuffle());
        loopBtn.setOnClickListener(v -> toggleLoop());
        btnBack.setOnClickListener(v -> onBackButtonClicked());


        rotateAnimator = ObjectAnimator.ofFloat(tvImage, View.ROTATION, 0f, 360f);
        rotateAnimator.setDuration(10000); // Xoay trong 10 giây
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        // Các phương thức khác...
        initializeViewTreeOwners();
        songIdList = new ArrayList<>();
        getDataFromIntent();
    }

    private void onBackButtonClicked() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        stopRotateAnimation();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        finish();
    }
    private void toggleShuffle() {
        isShuffleOn = !isShuffleOn;
        shuffleBtn.setImageResource(isShuffleOn ? R.drawable.baseline_shuffle_24 : R.drawable.baseline_shuffle_24);

        if (isShuffleOn) {
            isLoopOn = false;
            loopBtn.setImageResource(R.drawable.baseline_loop_24);
        }
    }

    private void toggleLoop() {
        isLoopOn = !isLoopOn;
        loopBtn.setImageResource(isLoopOn ? R.drawable.baseline_loop_24 : R.drawable.baseline_loop_24);

        if (isLoopOn) {

            isShuffleOn = false;
            shuffleBtn.setImageResource(R.drawable.baseline_shuffle_24);
        }
    }

    private void getDataFromIntent() {
        songIdList = getIntent().getStringArrayListExtra("songIdList");
        position = getIntent().getIntExtra("position", -1);
        if (songIdList != null && position != -1) {
            loadSongs(position);
        } else {
            Log.e("MusicPlayer", "Không nhận được songIdList hoặc position");
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadSongs(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final int[] loadedCount = {0};
        final long startTime = System.currentTimeMillis();
        final long TIMEOUT = 30000; // 30 seconds timeout

        String songId = songIdList.get(position);
        FirebaseFirestore.getInstance().collection("Songs").document(songId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Song song = documentSnapshot.toObject(Song.class);
                    if (song != null) {
                        song.setKey(songId);
                        currentSong = song;
                        initializeMusicPlayer();
                    } else {
                        Log.e("MusicPlayer", "Song data is null for id: " + songId);
                        Toast.makeText(this, "Failed to load song", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MusicPlayer", "Error loading song: " + songId, e);
                    Toast.makeText(this, "Error loading song", Toast.LENGTH_SHORT).show();
                });
    }


    private void checkLoadCompletion(int totalCount, int loadedCount, long startTime) {
        if (loadedCount == totalCount || System.currentTimeMillis() - startTime > TIMEOUT) {
            if (musicList.isEmpty()) {
                Log.e("MusicPlayer", "Failed to load any songs");
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to load songs", Toast.LENGTH_LONG).show();
                    finish();  // Close the activity if no songs were loaded
                });
            } else {
                Log.d("MusicPlayer", "Loaded " + musicList.size() + " out of " + totalCount + " songs");
                runOnUiThread(() -> initializeMusicPlayer());
            }
        }
    }

    private void initializeMusicPlayer() {
        try {
            String songName = currentSong.getNameSong();
            String singer = currentSong.getSinger();
            String imageUrl = currentSong.getImage();
            String mp3Url = currentSong.getMP3();
            int duration = currentSong.getDuration();

            tvTitle.setText(songName);
            tvArtist.setText(singer);
            tvDuration.setText(millisecondsToString(duration));

            Glide.with(this)
                    .load(imageUrl)
                    .into(tvImage);

            setBlurredBackground(imageUrl);

            Uri uri = Uri.parse(mp3Url);
            if (uri == null) {
                throw new IllegalArgumentException("Invalid song URI");
            }

            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
            mMediaPlayer = MediaPlayer.create(this, uri);
            if (mMediaPlayer == null) {
                throw new IllegalStateException("Failed to initialize MediaPlayer");
            }

            mMediaPlayer.setOnPreparedListener(mp -> {
                seekBarTime.setMax(mMediaPlayer.getDuration());
                tvDuration.setText(millisecondsToString(mMediaPlayer.getDuration()));
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                mMediaPlayer.start();
                setupSeekBars();
                updateSeekBarTime();
                startRotateAnimation();
            });

            mMediaPlayer.setOnCompletionListener(mp -> {
                btnPlay.setBackgroundResource(R.drawable.ic_play);
                stopRotateAnimation();
                if (isLoopOn) {
                    loadSongs(position);
                } else if (isShuffleOn) {
                    playRandomSong();
                } else {
                    playNextSong();
                }
            });

        } catch (Exception e) {
            Log.e("MusicPlayer", "Error initializing music player", e);
            Toast.makeText(this, "Error playing music: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public void playClicked() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                btnPlay.setBackgroundResource(R.drawable.ic_play);
                pauseRotateAnimation();
            } else {
                mMediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                resumeRotateAnimation();
            }
        }
    }

    private void setBlurredBackground(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .apply(new RequestOptions().transform(new BlurTransformation(25, 3)))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getResources(), resource);
                        findViewById(R.id.layout).setBackground(drawable);  // Đảm bảo layout đã được khởi tạo
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void playCurrentSong() {
        initializeMusicPlayer();
    }

    private void playRandomSong() {
        Random rand = new Random();
        int newPosition;
        do {
            newPosition = rand.nextInt(songIdList.size());
        } while (newPosition == position && songIdList.size() > 1);
        position = newPosition;
        loadSongs( position );
    }

    private void playNextSong() {
        if (songIdList != null && songIdList.size() > 0) {
            position = (position + 1) % songIdList.size();
            loadSongs(position);
        }
    }

    private void playPreviousSong() {
        if (songIdList != null && songIdList.size() > 0) {
            position = (position - 1 + songIdList.size()) % songIdList.size();
            loadSongs(position);
        }
    }


    private void setupSeekBars() {
        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                if (mMediaPlayer != null) {  // Kiểm tra null
                    mMediaPlayer.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mMediaPlayer != null) {
                    mMediaPlayer.seekTo( progress );
                    if (tvTime != null) {
                        tvTime.setText( millisecondsToString( progress ) );
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateSeekBarTime() {
        if (handler != null && mMediaPlayer != null) {
            handler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        int currentPosition = mMediaPlayer.getCurrentPosition();
                        if (seekBarTime != null) {
                            seekBarTime.setProgress( currentPosition );
                        }
                        if (tvTime != null) {
                            tvTime.setText( millisecondsToString( currentPosition ) );
                        }
                        if (tvDuration != null) {
                            tvDuration.setText(millisecondsToString(mMediaPlayer.getDuration()));
                        }
                    }
                    handler.postDelayed( this, 1000 );
                }
            }, 0 );
        }
    }

    @Override
    public void nextClicked() {
        if (isShuffleOn) {
            playRandomSong();
        } else {
            position = (position + 1) % musicList.size();
            initializeMusicPlayer();
        }
    }

    @Override
    public void prevClicked() {
        playPreviousSong();
    }
    private void startRotateAnimation() {
        if (rotateAnimator != null && !rotateAnimator.isRunning()) {
            rotateAnimator.start();
        }
    }

    private void stopRotateAnimation() {
        if (rotateAnimator != null) {
            rotateAnimator.cancel();
            tvImage.setRotation(0f); // Đặt lại góc xoay về 0
        }
    }

    private void pauseRotateAnimation() {
        if (rotateAnimator != null) {
            rotateAnimator.pause();
        }
    }

    private void resumeRotateAnimation() {
        if (rotateAnimator != null) {
            rotateAnimator.resume();
        }
    }


    private String millisecondsToString(int milliseconds) {
        StringBuilder time = new StringBuilder();
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;

        if (minutes < 10) {
            time.append("0").append(minutes);
        } else {
            time.append(minutes);
        }

        time.append(":");
        if (seconds < 10) {
            time.append("0").append(seconds);
        } else {
            time.append(seconds);
        }

        return time.toString();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Kết nối dịch vụ
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // Ngắt kết nối dịch vụ
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages( null );
        }
        stopRotateAnimation();
    }

}
