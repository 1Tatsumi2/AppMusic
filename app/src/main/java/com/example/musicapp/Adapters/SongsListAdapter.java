package com.example.musicapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicapp.Activities.MusicPlayer;
import com.example.musicapp.Class.Song;
import com.example.musicapp.R;
import com.example.musicapp.databinding.SongListItemRecylerRowBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.MyViewHolder> {

    private List<String> songIdList;
    private final Context context;

    public SongsListAdapter(List<String> songIdList, Context context) {
        this.songIdList = songIdList;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final SongListItemRecylerRowBinding binding;

        public MyViewHolder(SongListItemRecylerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(String songId, Context context, List<String> songIdList, int position) {
            FirebaseFirestore.getInstance().collection("Songs")
                    .document(songId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Song song = documentSnapshot.toObject(Song.class);
                        if (song != null) {
                            binding.songTitleTextView.setText(song.getNameSong());
                            binding.songSubtitleTextView.setText(song.getSinger());
                            Glide.with(binding.songCoverImageView.getContext())
                                    .load(song.getImage())
                                    .apply(new RequestOptions().transform(new RoundedCorners(32)))
                                    .into(binding.songCoverImageView);

                            // Set click listener for each song item
                            binding.getRoot().setOnClickListener(v -> {
                                Intent intent = new Intent(context, MusicPlayer.class);
                                intent.putStringArrayListExtra("songIdList", new ArrayList<>(songIdList));
                                intent.putExtra("position", position);
                                context.startActivity(intent);
                            });
                        } else {
                            Log.e("SongsListAdapter", "Không tìm thấy bài hát với ID: " + songId);
                            binding.songTitleTextView.setText("Bài hát không tìm thấy");
                            binding.songSubtitleTextView.setText("");
                            binding.songCoverImageView.setImageResource(0);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SongsListAdapter", "Lỗi khi lấy dữ liệu từ Firestore", e);
                        binding.songTitleTextView.setText("Lỗi tải bài hát");
                        binding.songSubtitleTextView.setText("");
                        binding.songCoverImageView.setImageResource(0);
                    });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SongListItemRecylerRowBinding binding = SongListItemRecylerRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(songIdList.get(position), context, songIdList, position);
    }

    @Override
    public int getItemCount() {
        return songIdList.size();
    }
}