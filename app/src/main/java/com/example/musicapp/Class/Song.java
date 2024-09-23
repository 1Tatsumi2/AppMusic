package com.example.musicapp.Class;

import java.io.Serial;
import java.io.Serializable;

public class Song implements Serializable {

    private String NameSong;
    private String Singer;
    private String MP3;
    private int Duration;
    private String Image;
    private String album;
    private String key;
    private String Video;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Song(String NameSong, String singer, String mp3, int Duration, String image, String album, String video) {
        this.NameSong = NameSong;
        this.Singer = singer;
        this.MP3 = mp3;
        this.Duration = Duration;
        this.Image = image;
        this.album = album;
        this.Video = video;
    }

    public String getNameSong() {
        return NameSong;
    }

    public void setNameSong(String nameSong) {
        NameSong = nameSong;
    }

    public String getSinger() {
        return Singer;
    }

    public void setSinger(String singer) {
        Singer = singer;
    }

    public String getMP3() {
        return MP3;
    }

    public void setMP3(String MP3) {
        this.MP3 = MP3;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }
}

