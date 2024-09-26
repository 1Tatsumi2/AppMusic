package com.example.musicapp.Models;

import java.util.Objects;

public class CategoryModel {
    private String name;
    private String coverUrl;

    // Constructor với tham số
    public CategoryModel(String name, String coverUrl) {
        this.name = name;
        this.coverUrl = coverUrl;
    }

    // Constructor mặc định
    public CategoryModel() {
        this.name = "";
        this.coverUrl = "";
    }

    // Getters và Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    // Override phương thức toString()
    @Override
    public String toString() {
        return "CategoryModel{" +
                "name='" + name + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }

    // Override phương thức equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryModel that = (CategoryModel) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(coverUrl, that.coverUrl);
    }

    // Override phương thức hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(name, coverUrl);
    }
}
