package com.example.musicapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.musicapp.Adapters.CategoryAdapter;
import com.example.musicapp.Models.CategoryModel;
import com.example.musicapp.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng View Binding cho HomeFragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Gọi hàm lấy dữ liệu từ Firestore
        getCategories();

        return binding.getRoot();
    }

    // Lấy danh sách category từ Firestore
    private void getCategories() {
        FirebaseFirestore.getInstance().collection("category")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Chuyển dữ liệu từ Firestore thành danh sách CategoryModel
                    List<CategoryModel> categoryList = querySnapshot.toObjects(CategoryModel.class);

                    // Thiết lập RecyclerView để hiển thị dữ liệu
                    setupCategoryRecyclerView(categoryList);
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi việc lấy dữ liệu thất bại
                    e.printStackTrace();
                });
    }

    // Thiết lập RecyclerView với danh sách category
    private void setupCategoryRecyclerView(List<CategoryModel> categoryList) {
        // Khởi tạo adapter
        categoryAdapter = new CategoryAdapter(categoryList);

        // Sử dụng LinearLayoutManager với chiều ngang
        binding.categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Gán adapter cho RecyclerView
        binding.categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
