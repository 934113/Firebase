package com.example.firebase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {

    private static FirebaseFirestore db;

    public static FirebaseFirestore getDatabase() {

        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }

        return db;
    }
}
