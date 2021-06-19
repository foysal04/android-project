package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    FirebaseUser mUser = database.getUser();
    DocumentReference mainRef = firestore.collection("Users").document(mUser.getUid());
    FirebaseAuth mAuth = database.getFirebaseAuth();

    EditText editNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);
        editNameText = (EditText) findViewById(R.id.editNameText);

        setNameToTextview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAfterTransition();
    }

    void setButtonVisible(Button button){
        button.setClickable(true);
        button.setVisibility(View.VISIBLE);
    }

    void setButtonInvisible(Button button){
        button.setVisibility(View.INVISIBLE);
        button.setClickable(false);
    }

    public void onSaveNameClick(View view){
        Button editNameButton = (Button) findViewById(R.id.editNameButton);
        Button saveNameButton = (Button) findViewById(R.id.saveNameButton);

        String updatedName = editNameText.getText().toString();
        mainRef.update("username", updatedName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("Update", "Successfully updated");
                    }
                });

        editNameText.setText(updatedName);

        editNameText.setEnabled(false);

        setButtonVisible(editNameButton);
        setButtonInvisible(saveNameButton);
    }

    public void onEditNameClick(View view){
        Button editNameButton = (Button) findViewById(R.id.editNameButton);
        Button saveNameButton = (Button) findViewById(R.id.saveNameButton);

        setButtonInvisible(editNameButton);
        setButtonVisible(saveNameButton);

        editNameText.setEnabled(true);
    }

    public void onLogOutClick(View view){
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        mAuth.signOut();
        startActivity(intent);
    }

    private void setNameToTextview()
    {
        mainRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();

                        editNameText.setText((String) data.get("Name"));
                    }
                });
    }
}