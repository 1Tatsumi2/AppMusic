package com.example.musicapp.MusicManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.musicapp.UploadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.musicapp.R;
import com.example.musicapp.Class.Song;
import com.example.musicapp.Adapter.SongsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MusicManagerActivity extends AppCompatActivity {

    private static final String TAG = "MusicManagerActivity";
    private ArrayList<Song> songArrayList;
    private ListView lvSongs;
    private SearchView searchView;
    private FloatingActionButton fab;
    private SongsAdapter songsAdapter;
    private FirebaseFirestore db;
    private CollectionReference songsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_manager);

        initializeViews();
        initializeFirestore();
        setupListView();
        fetchSongsFromFirestore();
        setupSearchView();
        setupFabListener();
        setupListViewItemClickListener();

        Log.d(TAG, "onCreate completed");
    }

    private void initializeViews() {
        lvSongs = findViewById(R.id.lvSongs);
        searchView = findViewById(R.id.search);
        fab = findViewById(R.id.fab);
        songArrayList = new ArrayList<>();
    }

    private void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
        songsRef = db.collection("Songs");
    }

    private void setupListView() {
        songsAdapter = new SongsAdapter(this, songArrayList);
        lvSongs.setAdapter(songsAdapter);
    }

    private void fetchSongsFromFirestore() {
        songsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        songArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Song song = document.toObject(Song.class);
                                song.setKey(document.getId()); // Gán document ID làm giá trị cho trường Key

                                // Kiểm tra nếu chưa có Key trong Firestore
                                if (!document.contains("Key")) {
                                    // Cập nhật Key trong tài liệu
                                    songsRef.document(document.getId())
                                            .update("Key", document.getId())
                                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Key field updated for document " + document.getId()))
                                            .addOnFailureListener(e -> Log.e(TAG, "Error updating Key field", e));
                                }
                                songArrayList.add(song);
                                Log.d(TAG, "Added song: " + song.getNameSong() + " with key: " + song.getKey());
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting document: ", e);
                            }
                        }
                        songsAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Total songs: " + songArrayList.size());
                    } else {
                        Log.e(TAG, "Error fetching data: ", task.getException());
                        showErrorToast("Cannot load song list");
                    }
                });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    songsAdapter.searchSongLst(songArrayList);
                } else {
                    searchList(newText);
                }
                return true;
            }
        });
    }

    private void setupFabListener() {
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MusicManagerActivity.this, UploadActivity.class);
            startActivity(intent);
        });
    }

    private void setupListViewItemClickListener() {
        lvSongs.setOnItemClickListener((adapterView, view, position, l) -> {
            try {
                Song song = songsAdapter.getItem(position);
                if (song != null && song.getKey() != null) {
                    Intent intent = new Intent(MusicManagerActivity.this, MusicDetailActivity.class);

                    // Pass the Key stored in Firestore
                    intent.putExtra("Key", song.getKey());
                    intent.putExtra("NameSong", song.getNameSong());
                    intent.putExtra("Artist", song.getArtist());
                    intent.putExtra("Singer", song.getSinger());
                    intent.putExtra("MP3", song.getMP3());
                    intent.putExtra("Image", song.getImage());
                    intent.putExtra("Duration", song.getDuration());

                    Log.d(TAG, "Opening MusicDetailActivity with key: " + song.getKey());
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling item click: ", e);
                showErrorToast("Error opening song details");
            }
        });
    }

    private void searchList(String text) {
        ArrayList<Song> searchResults = new ArrayList<>();
        for (Song data : songArrayList) {
            if (data.getNameSong().toLowerCase().contains(text.toLowerCase()) ||
                    data.getSinger().toLowerCase().contains(text.toLowerCase())) {
                searchResults.add(data);
            }
        }
        songsAdapter.searchSongLst(searchResults);
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSongsFromFirestore();
    }
}