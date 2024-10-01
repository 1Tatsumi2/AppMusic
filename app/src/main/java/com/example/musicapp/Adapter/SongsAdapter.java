package com.example.musicapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicapp.R;
import com.example.musicapp.Class.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends ArrayAdapter<Song> {
    private List<Song> songList;  // List to hold songs

    // Constructor
    public SongsAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
        this.songList = objects;  // Initialize songList with the objects passed
    }

    @Nullable
    @Override
    public Song getItem(int position) {
        return songList.get(position);  // Use the correct list
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, null);

        TextView tvNameSong = convertView.findViewById(R.id.tvNameSong);
        TextView tvSinger = convertView.findViewById(R.id.tvSinger);

        Song song = getItem(position);  // Fetch song from the correct list
        if (song != null) {
            tvSinger.setText(song.getSinger());
            tvNameSong.setText(song.getNameSong());
        }

        return convertView;
    }

    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    @Override
    public int getCount() {
        return songList != null ? songList.size() : 0;  // Check for null to avoid NullPointerException
    }

    // Method to search and update the song list
    public void searchSongLst(ArrayList<Song> searchList) {
        songList.clear();
        songList.addAll(searchList);
        notifyDataSetChanged();  // Notify adapter to refresh the list
    }
}

