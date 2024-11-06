package com.example.musicapp.Playlist;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicapp.Class.Playlist;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.content.Intent;
import android.net.Uri;

public class AddPlaylistActivity extends AppCompatActivity {

    private EditText playlistName, playlistDescription;
    private ImageView playlistImage;
    private Button addPlaylistButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        playlistName = findViewById(R.id.playlist_name);
        playlistDescription = findViewById(R.id.playlist_description);
        playlistImage = findViewById(R.id.imagePlaylist);
        addPlaylistButton = findViewById(R.id.add_playlist_button);

        playlistImage.setOnClickListener(v -> openImageChooser());

        addPlaylistButton.setOnClickListener(v -> addPlaylist());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            playlistImage.setImageURI(imageUri);
        }
    }

    private void addPlaylist() {
        String name = playlistName.getText().toString().trim();
        String description = playlistDescription.getText().toString().trim();
        String userId = auth.getCurrentUser().getUid();

        if (name.isEmpty()) {
            Toast.makeText(this, "Tên playlist không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageToFirebase(name, description, userId);
        } else {
            savePlaylistToFirestore(name, description, userId, null);
        }
    }

    private void uploadImageToFirebase(String name, String description, String userId) {
        if (imageUri == null) {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("playlist_images/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> savePlaylistToFirestore(name, description, userId, uri.toString()))
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lấy URL ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void savePlaylistToFirestore(String name, String description, String userId, String imageUrl) {
        DocumentReference authorReference = db.collection("Account").document(userId);

        Playlist newPlaylist = new Playlist(name, description, imageUrl, authorReference, true, "My Playlist", 0);

        db.collection("Playlists").add(newPlaylist)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Tạo playlist thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Tạo playlist thất bại", Toast.LENGTH_SHORT).show()
                );
    }
}
