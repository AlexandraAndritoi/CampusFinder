package com.upt.ac.campusfinderapp.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.ac.campusfinderapp.model.User;

public class UserRepository {

    public void saveUser(String id, String username, String email) {
        User user = new User(username, email);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("user/" + id);
        mDatabase.setValue(user);
    }
}
