package com.example.musicapp.Playlist;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapp.Adapter.AddSongsToPlaylistAdapter;
import com.example.musicapp.Class.Song;
import com.example.musicapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddSongToPlaylistActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private AddSongsToPlaylistAdapter songsAdapter;
    private List<Song> songList = new ArrayList<>();
    private FirebaseFirestore db;
    private String playlistId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_to_playlist);

        db = FirebaseFirestore.getInstance();
        songsRecyclerView = findViewById(R.id.songsRecyclerView);
        playlistId = getIntent().getStringExtra("playlistId");

        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songsAdapter = new AddSongsToPlaylistAdapter(this, songList);
        songsRecyclerView.setAdapter(songsAdapter);

        songsAdapter.setOnItemClickListener(song -> addSongToPlaylist(song));
        loadSongs();
    }

    private void loadSongs() {
        db.collection("Songs").get().addOnSuccessListener(queryDocumentSnapshots -> {
            songList.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Song song = document.toObject(Song.class);
                if (song != null) {
                    songList.add(song);
                }
            }
            songsAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể tải danh sách bài hát", Toast.LENGTH_SHORT).show();
        });
    }


    private void addSongToPlaylist(Song song) {
        if (song.getKey() == null || song.getKey().isEmpty()) {
            Toast.makeText(this, "Bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy tham chiếu đến playlist
        DocumentReference playlistRef = db.collection("Playlists").document(playlistId);

        // Cập nhật danh sách bài hát và tăng số lượng bài hát
        playlistRef.update("songs", FieldValue.arrayUnion(song.getKey()))
                .addOnSuccessListener(aVoid -> {
                    // Tăng giá trị của songNumber
                    playlistRef.update("songNumber", FieldValue.increment(1))
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Đã thêm bài hát vào Playlist", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Không thể cập nhật số lượng bài hát", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể thêm bài hát", Toast.LENGTH_SHORT).show();
                });
    }

}
