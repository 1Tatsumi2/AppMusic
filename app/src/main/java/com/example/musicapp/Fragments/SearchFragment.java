package com.example.musicapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.musicapp.Activities.MusicPlayer;
import com.example.musicapp.Adapter.SongsAdapter;
import com.example.musicapp.Class.Song;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {


    List<Song> songArrayList;
    ListView lvSongs;
    SearchView searchView;

    SongsAdapter songsAdapter;
    String receivedString;
    Boolean isSth;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("Music");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        lvSongs = view.findViewById(R.id.lvSongs);
        searchView=view.findViewById(R.id.search);

        songArrayList = new ArrayList<>();

        songsAdapter = new SongsAdapter(getActivity(), songArrayList);
        lvSongs.setAdapter(songsAdapter);
        Bundle arguments = getArguments();
        if (arguments != null) {
            receivedString = arguments.getString("search");
            isSth=arguments.getBoolean("isSth");
        }
        if (isSth==null)
        {
            isSth=false;
        }
        showAllSongs();
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    Log.d("FirebaseData", "Number of documents: " + queryDocumentSnapshots.size());
                    songArrayList.clear();  // Clear the list before adding new data
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Song song = documentSnapshot.toObject(Song.class);
                        song.setKey(documentSnapshot.getId());
                        songArrayList.add(song);
                        Log.d("FirebaseData", "Song added: " + song.getNameSong());
                    }
                    songsAdapter.notifyDataSetChanged();
                } else {
                    Log.d("FirebaseData", "No documents found");
                }

                if (isSth) {
                    searchView.setQuery(receivedString, false);
                    searchList(receivedString);
                    isSth = false;
                } else {
                    showAllSongs();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showAllSongs();
                } else {
                    searchList(newText);
                }
                return true;
            }
        });

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Song song = songsAdapter.getItem(position);
                int originalPosition = songArrayList.indexOf(song);
                Intent openMusicPlayer = new Intent(getActivity(), MusicPlayer.class);
                openMusicPlayer.putExtra("song", song);
                openMusicPlayer.putExtra("musics", (Serializable) songArrayList);
                openMusicPlayer.putExtra("key",song.getKey());
                openMusicPlayer.putExtra("position",originalPosition);
                startActivity(openMusicPlayer);
            }
        });

        return view;
    }
    public void searchList(String text)
    {
        ArrayList<Song> searchList=new ArrayList<>();
        for(Song data:songArrayList)
        {
            if(data.getNameSong().toLowerCase().contains(text.toLowerCase()) ||
                    data.getSinger().toLowerCase().contains(text.toLowerCase()))
            {
                searchList.add(data);
            }
        }
        songsAdapter.searchSongLst(searchList);
    }
    public void showAllSongs() {
        songsAdapter.searchSongLst((ArrayList<Song>) songArrayList);
    }
}