package com.example.musicapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.musicapp.Class.Playlist;
import com.example.musicapp.R;
import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private List<Playlist> playlists;

    public PlaylistAdapter(@NonNull Context context, int resource, @NonNull List<Playlist> objects) {
        super(context, resource, objects);
        this.playlists = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        }

        TextView playlistTitle = convertView.findViewById(R.id.namePlaylist);
        TextView playlistAuthor = convertView.findViewById(R.id.authorPlaylist);
        TextView playlistNumSong = convertView.findViewById(R.id.songPlaylist);
        ImageView image = convertView.findViewById(R.id.imagePlaylist);

        Playlist playlist = getItem(position);

        playlistTitle.setText(playlist.getName());
        playlistNumSong.setText("Số lượng bài hát: " + playlist.getSongNumber()); // Thêm nhãn cho số lượng bài hát

        // Hiển thị tên tác giả từ DocumentReference
        playlist.getAuthor().get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String authorName = documentSnapshot.getString("name"); // Thay "name" bằng tên trường chứa tên tác giả trong Firestore
                playlistAuthor.setText("Tác giả: " + authorName);
            } else {
                playlistAuthor.setText("Unknown");
            }
        }).addOnFailureListener(e -> {
            playlistAuthor.setText("Error");
        });

        // Hiển thị ảnh playlist
        Glide.with(getContext()).load(playlist.getImage()).into(image);

        return convertView;
    }


}
