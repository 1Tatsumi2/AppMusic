package com.example.musicapp.Playlist;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapp.Adapter.PlaylistSongsAdapter;
import com.example.musicapp.Class.Song;
import com.example.musicapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    private TextView playlistName, playlistDescription;
    private RecyclerView songsRecyclerView;
    private PlaylistSongsAdapter playlistSongsAdapter;
    private FirebaseFirestore db;
    private List<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        playlistName = findViewById(R.id.playlist_name);
        playlistDescription = findViewById(R.id.playlist_description);
        songsRecyclerView = findViewById(R.id.songs_recycler_view);
        db = FirebaseFirestore.getInstance();

        // Thiết lập RecyclerView
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistSongsAdapter = new PlaylistSongsAdapter(this, songList);
        songsRecyclerView.setAdapter(playlistSongsAdapter);

        // Nhận playlistId từ Intent
        String playlistId = getIntent().getStringExtra("playlistId");

        if (playlistId != null) {
            loadPlaylistDetails(playlistId);
        } else {
            Toast.makeText(this, "Không tìm thấy Playlist", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadPlaylistDetails(String playlistId) {
        DocumentReference playlistRef = db.collection("Playlists").document(playlistId);
        playlistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String description = documentSnapshot.getString("description");
                List<String> songIds = (List<String>) documentSnapshot.get("songs");

                playlistName.setText(name);
                playlistDescription.setText(description);

                // Tải danh sách bài hát từ songIds
                if (songIds != null) {
                    loadSongs(songIds);
                } else {
                    Toast.makeText(this, "Không có bài hát trong Playlist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Không thể tải chi tiết Playlist", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadSongs(List<String> songIds) {
        songList.clear();
        for (String songId : songIds) {
            db.collection("Songs").document(songId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Song song = documentSnapshot.toObject(Song.class);
                    if (song != null) {
                        songList.add(song);
                    }
                }
                // Chỉ cập nhật adapter khi hoàn tất việc tải tất cả bài hát
                if (songList.size() == songIds.size()) {
                    playlistSongsAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Không thể tải bài hát: " + songId, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
