package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";

    Database database = Database.getInstance();

    FirebaseAuth mAuth = database.getFirebaseAuth();
    FirebaseUser mUser = database.getUser();
    FirebaseFirestore firestore = database.getFirestore();
    EditText emailField;
    EditText usernameField;
    EditText passwordField;
    Button goButton;

    String email;
    String username;
    String password;

    Map<String, String> userData;

    public void bindWidgets(){

        emailField = (EditText) findViewById(R.id.signUpEmailTextField);
        usernameField = (EditText) findViewById(R.id.signUpNameTextField);
        passwordField = (EditText) findViewById(R.id.signUpPasswordTextField);
        goButton = (Button) findViewById(R.id.goButton);

        goButton.setOnClickListener(this);
        userData = new HashMap<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindWidgets();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAfterTransition();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goButton: {
                username = usernameField.getText().toString();
                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                if (username.isEmpty()) {
                    usernameField.setError("Name cannot be empty");
                    usernameField.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    emailField.setError("Email cannot be empty");
                    emailField.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    passwordField.setError("Password must be greater than 6 characters");
                    passwordField.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailField.setError("Invalid email");
                    emailField.requestFocus();
                    return;
                }

                userData.put("email", email);
                userData.put("username", username);
                userData.put("password", password);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mUser = mAuth.getCurrentUser();
                                    mUser.sendEmailVerification();
                                    firestore.collection("Users")
                                            .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                            .set(userData);
                                    userData.clear();
                                    Toast.makeText(SignUpActivity.this, "Signed up!", Toast.LENGTH_SHORT).show();
                                    ManageUser.clear();
                                    ManageUser.setUser(username, email);
                                    startActivity(new Intent(SignUpActivity.this, HomePageActivity.class));
                                } else
                                    Toast.makeText(SignUpActivity.this, "An account exists with this email. Please out another unused email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            break;
        }
    }
}