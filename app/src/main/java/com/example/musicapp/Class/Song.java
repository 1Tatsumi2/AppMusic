package com.example.musicapp.Class;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

@SuppressLint("ParcelCreator")
public class Song implements Serializable, Parcelable {

    private String Album;
    private int Duration;
    private String Image;
    private String Key;
    private String MP3;
    private String NameSong;
    private String Singer;
    private String Video;
    private  String Artist;


    // Constructor mặc định (cần thiết cho Firebase)
    public Song(String nameUpdate, String singerUpdate, String audioUrl, int duration, String imageUrl, String albumUpdate, String videoUpdate, String artistUpdate) {}
    public  Song() {}
    // Constructor đầy đủ
    public Song(String Album, int Duration, String Image, String Key, String MP3, String NameSong, String Singer, String Video, String Artist) {
        this.Album = Album;
        this.Duration = Duration;
        this.Image = Image;
        this.Key = Key;
        this.MP3 = MP3;
        this.NameSong = NameSong;
        this.Singer = Singer;
        this.Video = Video;
        this.Artist = Artist;
    }

    // Getter và Setter
    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String Album) {
        this.Album = Album;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int Duration) {
        this.Duration = Duration;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public String getMP3() {
        return MP3;
    }

    public void setMP3(String MP3) {
        this.MP3 = MP3;
    }

    public String getNameSong() {
        return NameSong;
    }

    public void setNameSong(String NameSong) {
        this.NameSong = NameSong;
    }

    public String getSinger() {
        return Singer;
    }

    public void setSinger(String Singer) {
        this.Singer = Singer;
    }

    public String getVideo()     {
        return Video;
    }

    public void setVideo(String Video) {
        this.Video = Video;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    protected Song(Parcel in) {
        Album = in.readString();
        Duration = in.readInt();
        Image = in.readString();
        Key = in.readString();
        MP3 = in.readString();
        NameSong = in.readString();
        Singer = in.readString();
        Video = in.readString();
        Artist = in.readString();
    }
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(Album);
        dest.writeInt(Duration);
        dest.writeString(Image);
        dest.writeString(Key);
        dest.writeString(MP3);
        dest.writeString(NameSong);
        dest.writeString(Singer);
        dest.writeString(Video);
        dest.writeString( Artist );
    }

}

