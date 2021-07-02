package com.example.afinal;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

// A singleton to initialize and call the Firestore database
public class Database {
    private static final Database database = new Database();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public static String username = "";

    public static Database getInstance()
    {
        return database;
    }

    public FirebaseAuth getFirebaseAuth()
    {
        return mAuth;
    }

    public FirebaseFirestore getFirestore()
    {
        return firestore;
    }

    public FirebaseUser getUser()
    {
        return mUser;
    }

    public void setUsername(String _username)
    {
        username = _username;
    }

    public FirebaseStorage getStorage(){ return storage; }
}
