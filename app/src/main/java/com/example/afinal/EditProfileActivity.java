package com.example.afinal;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    FirebaseUser mUser = database.getUser();
    DocumentReference mainRef = firestore.collection("Users").document(mUser.getUid());
    FirebaseAuth mAuth = database.getFirebaseAuth();

    Button editProfile;
    EditText editNameText;
    Button editNameButton;
    Button saveNameButton;
    Button logoutButton;
    ImageView profileImage;
    PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);
        editNameText = (EditText) findViewById(R.id.editNameText);

        editNameButton = (Button) findViewById(R.id.editNameButton);
        saveNameButton = (Button) findViewById(R.id.saveNameButton);
        logoutButton = (Button) findViewById(R.id.logOutButton);
        editProfile = (Button) findViewById(R.id.editProfilePicture);
        profileImage = (ImageView) findViewById(R.id.profileImage);

        popupMenu = new PopupMenu(getApplicationContext(), editProfile);

        popupMenu.getMenu().add("Take photo");
        popupMenu.getMenu().add("Select from storage");

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

    public void onEditPictureClick(View view)
    {
        Log.i("Click", "Edit picture");

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equals("Take photo")) {
                    Log.i("popup", item.getTitle().toString());
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                    } else {
                        Log.i("camera", "Start camera");

//                        ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(
//                                new ActivityResultContracts.StartActivityForResult(),
//                                new ActivityResultCallback<ActivityResult>() {
//                                    @Override
//                                    public void onActivityResult(ActivityResult result) {
//                                        if (result.getResultCode() == Activity.RESULT_OK) {
//                                            // There are no request codes
//                                            Intent data = result.getData();
//                                            Bitmap photo = (Bitmap) data.getExtras().get("data");
//                                            profileImage.setImageBitmap(photo);
//                                        }
//                                    }
//                                });

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivity(cameraIntent);

                        startActivityForResult(cameraIntent, 2);
                    }
                }
                return true;
            }
        });

        popupMenu.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == 2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(cameraIntent);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(photo);
        }
    }
}