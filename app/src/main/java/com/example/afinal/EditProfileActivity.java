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
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private final int CAMERA_REQUEST_CODE = 2;
    private final int INTERNAL_STORAGE_CODE = 3;

    Database database = Database.getInstance();
    FirebaseFirestore firestore = database.getFirestore();
    FirebaseUser mUser = database.getFirebaseAuth().getCurrentUser();
    DocumentReference mainRef = firestore.collection("Users").document(mUser.getUid());
    FirebaseAuth mAuth = database.getFirebaseAuth();
    StorageReference storageReference = database.getStorage().getReference();

    Button editProfile;
    EditText editNameText;
    Button editNameButton;
    Button saveNameButton;
    Button logoutButton;
    ImageView profileImage;
    PopupMenu popupMenu;
    String uid = mAuth.getCurrentUser().getUid();

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

        mainRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    String name = (String) value.get("username");
                    Log.i("Name", name);
                    editNameText.setText(name);

                    String imageDir = (String) value.get("Image");
                    if (imageDir != null) {
                        StorageReference profileImageRef = storageReference.child(imageDir);

                        final long ONE_MEGABYTE = 1024 * 1024;
                        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profileImage.setImageBitmap(bmp);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
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
                else
                {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, INTERNAL_STORAGE_CODE);
                    else
                    {
                        Intent storageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        storageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        storageIntent.setType("*/*");
                        startActivityForResult(storageIntent, 3);
                    }
                }
                return true;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0)
        {
            if(requestCode == CAMERA_REQUEST_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 2);
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
            }

            else if(requestCode == INTERNAL_STORAGE_CODE)
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "storage access permission granted", Toast.LENGTH_LONG).show();
                    Intent storageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    storageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    storageIntent.setType("*/*");
                    startActivityForResult(storageIntent, 3);
                } else {
                    Toast.makeText(this, "storage access permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    profileImage.setImageBitmap(photo);

                    StorageReference profileImageRef = storageReference.child("Users/" + uid + "/ProfileImage/profile_image.jpeg");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    UploadTask uploadTask = profileImageRef.putBytes(bitmapdata);
                    Log.i("aafa", profileImageRef.getStorage().toString());

                    mainRef.update("Image", "Users/" + uid + "/ProfileImage/profile_image.jpeg");

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.i("Profile image: ", "upload unsuccessful");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i("Profile image: ", taskSnapshot.getMetadata().toString());
                        }
                    });
                }
            }break;

            case INTERNAL_STORAGE_CODE:
            {
                if(resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();

                    StorageReference profileImageRef = storageReference.child("Users/" + uid + "/ProfileImage/profile_image.jpeg");

                    String path = getPathFromURI(uri);
                    if(path != null)
                    {
                        File file = new File(path);
                        uri = Uri.fromFile(file);
                    }

                    profileImage.setImageURI(uri);

                    mainRef.update("Image", "Users/" + uid + "/ProfileImage/profile_image.jpeg");

                    UploadTask uploadTask = profileImageRef.putFile(uri);
                    Log.i("aafap", profileImageRef.getMetadata().toString());
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.i("Profile image: ", "upload unsuccessful");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i("Profile image: ", taskSnapshot.getMetadata().toString());
                        }
                    });
                }
            }break;
        }
    }

    private String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}