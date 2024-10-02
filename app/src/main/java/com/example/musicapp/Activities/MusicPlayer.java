package com.example.musicapp.Activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener {

    int position, currentPos;
    TextView tvTittle, tvArtist, tvTime, tvDuration;
    CircleImageView tvImage;
    ImageView btnShuffle, btnLoop, previousBtn, back, nextBtn;
    ImageButton dotButton;
    static MediaPlayer mMediaPlayer;
    Button btnPlay;
    LinearLayout layout;
    SeekBar seekBarTime, seekBarVolume;
    ObjectAnimator objectAnimator;

    private boolean isShuffleOn = false;
    private boolean isLoopOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String songId = intent.getStringExtra("songId");
        String songTitle = intent.getStringExtra("songTitle");
        String songArtist = intent.getStringExtra("songArtist");
        String songUrl = intent.getStringExtra("songUrl"); // URL của bài nhạc, nếu có
        String songImage = intent.getStringExtra("songImage");

        tvTittle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvImage = findViewById(R.id.tvImage);
        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);
        btnShuffle = findViewById(R.id.shuffleBtn);
        btnLoop = findViewById(R.id.LoopBtn);
        previousBtn = findViewById(R.id.previous);
        btnPlay = findViewById(R.id.btnPlay);
        back = findViewById(R.id.btnBack);
        nextBtn = findViewById(R.id.next);
        dotButton = findViewById(R.id.dotButton);
        layout = findViewById(R.id.layout);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);

        // Thiết lập giao diện người dùng với dữ liệu nhận được
        tvTittle.setText(songTitle);
        tvArtist.setText(songArtist);

        // Nếu có hình ảnh của bài hát, hiển thị nó
        if (songImage != null) {
            Glide.with(this)
                    .load(songImage)
                    .apply(new RequestOptions().circleCrop())
                    .into(tvImage);
        }

        // Thiết lập MediaPlayer để phát nhạc từ URL của bài hát
        if (songUrl != null) {
            Uri songUri = Uri.parse(songUrl);  // URI của bài hát từ Firebase hoặc bất kỳ nguồn nào khác
            mMediaPlayer = MediaPlayer.create(this, songUri);
        } else {
            // Nếu không có URL, phát nhạc mặc định
            mMediaPlayer = MediaPlayer.create(this, R.raw.quaydiquaylai);  // Bài hát mặc định nếu không có URL
        }

        mMediaPlayer.setLooping(true);
        mMediaPlayer.seekTo(0);
        mMediaPlayer.setVolume(1.0f, 1.0f);  // Điều chỉnh âm lượng nếu cần

        // Cập nhật thời lượng bài hát lên giao diện
        String duration = millisecondsToString(mMediaPlayer.getDuration());
        tvDuration.setText(duration);

        // Sự kiện khi người dùng nhấn play/pause
        btnPlay.setOnClickListener(this);

        // Cài đặt SeekBar volume và seekBar thời gian
        setupVolumeControl();
        setupTimeControl();

        // Cập nhật tiến độ của bài hát theo thời gian
        updateSeekBar();
    }

    // Thiết lập SeekBar điều khiển âm lượng
    private void setupVolumeControl() {
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
    }

    // Thiết lập SeekBar điều khiển thời gian
    private void setupTimeControl() {
        seekBarTime.setMax(mMediaPlayer.getDuration());
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Cập nhật tiến độ của bài hát
    private void updateSeekBar() {
        new Thread(() -> {
            while (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    try {
                        final double current = mMediaPlayer.getCurrentPosition();
                        final String elapsedTime = millisecondsToString((int) current);

                        runOnUiThread(() -> {
                            tvTime.setText(elapsedTime);
                            seekBarTime.setProgress((int) current);
                        });

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // Định dạng thời gian từ milliseconds thành phút:giây
    public String millisecondsToString(int time) {
        String elapseTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapseTime = minutes + ":";
        if (seconds < 10) {
            elapseTime += "0";
        }
        elapseTime += seconds;
        return elapseTime;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnPlay) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                btnPlay.setBackgroundResource(R.drawable.ic_play);
            } else {
                mMediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
            }
        }
    }
}
