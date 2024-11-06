package com.example.musicapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.musicapp.Adapter.PlaylistAdapter;
import com.example.musicapp.Class.Playlist;
import com.example.musicapp.Playlist.PlaylistDetailActivity;
import com.example.musicapp.R;
import com.example.musicapp.Playlist.AddPlaylistActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private ListView lvPlaylists;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Playlist> playlistList = new ArrayList<>();
    private PlaylistAdapter playlistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        lvPlaylists = view.findViewById(R.id.lvPlaylists);
        playlistAdapter = new PlaylistAdapter(getContext(), R.layout.item_playlist, playlistList);
        lvPlaylists.setAdapter(playlistAdapter);

        // Sự kiện click vào icon thêm playlist
        view.findViewById(R.id.add_icon).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPlaylistActivity.class);
            startActivity(intent);
        });

        // Sự kiện click vào một playlist trong ListView
        lvPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist selectedPlaylist = playlistList.get(position);

                // Khởi tạo intent để chuyển sang PlaylistDetailActivity
                Intent intent = new Intent(getContext(), PlaylistDetailActivity.class);
                intent.putExtra("playlistId", selectedPlaylist.getKey()); // Gửi playlist ID sang Activity khác
                startActivity(intent);
            }
        });

        loadPlaylists();
        return view;
    }

    private void loadPlaylists() {
        db.collection("Playlists").get().addOnSuccessListener(queryDocumentSnapshots -> {
            playlistList.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Playlist playlist = document.toObject(Playlist.class);
                playlist.setKey(document.getId()); // Gán ID của document vào key
                playlistList.add(playlist);
            }
            playlistAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Không thể tải danh sách phát", Toast.LENGTH_SHORT).show();
        });
    }
}
