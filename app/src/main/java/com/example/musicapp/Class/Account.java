package com.example.musicapp.Class;

import com.example.musicapp.R;

public class Account {

        public String UserName;
        public String Email;
        public String Password;
        public String Image;
        public boolean Premium;
        public String Role;

        // Constructor mặc định cho Firestore
        public Account() {
        }

        public Account(String UserName, String Email, String Passwork, String Image, boolean Premium, String Role) {
            this.UserName = UserName;
            this.Email = Email;
            this.Image = Image;
            this.Password = Passwork;
            this.Premium = Premium;
            this.Role = Role;
        }
    }

