package com.example.musicapp.Activities;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicPlayer extends AppCompatActivity implements View.OnClickListener {

    Bundle songExtraData;
    int position, currentPos;
    TextView tvTittle, tvArtist, tvTime, tvDuration;
    CircleImageView tvImage;
    ImageView btnShuffle, btnLoop, previousBtn, back, nextBtn;
    ImageButton dotButton;
    static MediaPlayer mMediaPlayer;
    //    MusicService musicService;
    //    MediaSessionCompat mediaSession;
    String key;
    //    ArrayList<Song> musicList;
    Button btnPlay;
    LinearLayout layout;
    SeekBar seekBarTime, seekBarVolume;
    NotificationManager notificationManager;
    ObjectAnimator objectAnimator;


    private boolean isShuffleOn = false;
    private boolean isLoopOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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


        mMediaPlayer = MediaPlayer.create(this,R.raw.quaydiquaylai);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.seekTo(0);
        mMediaPlayer.setVolume(100f,0.5f);


        String duration = millisecondsToString(mMediaPlayer.getDuration());
        tvDuration.setText(duration);

        btnPlay.setOnClickListener(this);

        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress/100f;
                mMediaPlayer.setVolume(volume,volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarTime.setMax(mMediaPlayer.getDuration());
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mMediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    if(mMediaPlayer.isPlaying()) {
                        try{
                            final double current = mMediaPlayer.getCurrentPosition();
                            final String elapsedTime = millisecondsToString((int) current);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTime.setText(elapsedTime);
                                    seekBarTime.setProgress((int)current);

                                }
                            });

                            Thread.sleep(1000);
                        } catch(InterruptedException e){}
                    }
                }
            }
        }).start();
    } // end main


    public String millisecondsToString(int time){
        String elapseTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time/ 1000 % 60;
        elapseTime = minutes + ":";
        if(seconds < 10){
            elapseTime += "0";
        }
        elapseTime += seconds;
        return elapseTime;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnPlay){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
                btnPlay.setBackgroundResource(R.drawable.ic_play);
            } else{
                mMediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);

            }
        }
    }
}




