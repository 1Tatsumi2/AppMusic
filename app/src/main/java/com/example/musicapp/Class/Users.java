package com.example.musicapp.Class;

import java.io.Serializable;

public class Users implements Serializable {
    private String fName;
    private String name;
    private String email;
    private String image;
    private String role;
    private String key;
    private Boolean premium;

    public Users(String fName, String name, String email, String image, String role,Boolean premium) {
        this.fName = fName;
        this.name = name;
        this.email = email;
        this.image = image;
        this.role = role;
        this.premium=premium;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getName() {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }
}
