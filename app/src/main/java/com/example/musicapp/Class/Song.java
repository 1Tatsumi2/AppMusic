package com.example.musicapp.Class;

import java.io.Serial;
import java.io.Serializable;

public class Song implements Serializable {

    private String title;
    private String artist;
    private String path;
    private int duration;
    private String image;
    private String album;
    private String key;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Song(String title, String artist, String path, int duration, String image, String album) {
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
        this.image = image;
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
