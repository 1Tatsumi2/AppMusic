package com.example.musicapp.Class;

import com.google.firebase.firestore.DocumentReference;
import java.util.List;

public class Playlist {
    private String name;
    private String description;
    private Boolean isPublic;
    private DocumentReference author; // Tham chiếu đến tài liệu người dùng trong Firestore
    private String image; // URL của ảnh đại diện playlist
    private Integer songNumber; // Số lượng bài hát trong playlist
    private List<String> songs; // Danh sách các ID bài hát
    private String classified; // Phân loại playlist (ví dụ: "My Playlist")
    private String key; // Key duy nhất của playlist

    // Constructor mặc định (bắt buộc khi sử dụng Firestore)
    public Playlist() {}

    // Constructor đầy đủ để tạo Playlist mới
    public Playlist(String name, String description, Boolean isPublic, DocumentReference author,
                    String image, Integer songNumber, List<String> songs, String classified, String key) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.author = author;
        this.image = image;
        this.songNumber = songNumber;
        this.songs = songs;
        this.classified = classified;
        this.key = key;
    }
    public Playlist(String name, String description, String image, DocumentReference author, Boolean isPublic, String classified, Integer songNumber) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.author = author;
        this.isPublic = isPublic;
        this.classified = classified;
        this.songNumber = songNumber;
    }

    // Getters và setters cho các trường
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public DocumentReference getAuthor() { return author; }
    public void setAuthor(DocumentReference author) { this.author = author; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getSongNumber() { return songNumber; }
    public void setSongNumber(Integer songNumber) { this.songNumber = songNumber; }

    public List<String> getSongs() { return songs; }
    public void setSongs(List<String> songs) { this.songs = songs; }

    public String getClassified() { return classified; }
    public void setClassified(String classified) { this.classified = classified; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
