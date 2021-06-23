package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    Database database = Database.getInstance();

    FirebaseAuth mAuth = database.getFirebaseAuth();
    FirebaseUser mUser = database.getUser();
    FirebaseFirestore firestore = database.getFirestore();

    EditText emailField;
    EditText passwordField;

    Button loginButton;
    TextView signupView;
    TextView resetPassword;

    String email;
    String password;

    public void bindWidgets(){
        emailField = (EditText) findViewById(R.id.logInEmailText);
        passwordField = (EditText) findViewById(R.id.logInPasswordText);
        loginButton = (Button) findViewById(R.id.loginButton);
        signupView = (TextView) findViewById(R.id.signUpTextView);
        resetPassword = (TextView) findViewById(R.id.resetPasswordTextView);

        loginButton.setOnClickListener(this);
        signupView.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
    }

    public void onSignUpButtonLogInActivityClick(View view){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        finish();
        finishAffinity();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindWidgets();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton: {
                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                if (email.isEmpty()) {
                    emailField.setError("Email cannot be empty");
                    emailField.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordField.setError("Invalid password");
                    passwordField.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailField.setError("Invalid email. Please type in valid email");
                    emailField.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser mUser = mAuth.getCurrentUser();
                                    assert mUser != null;
                                    if (mUser.isEmailVerified()) {
                                        DocumentReference userRef = database.getFirestore().collection("Users")
                                                .document(mAuth.getCurrentUser().getUid());

                                        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                Map<String, Object> data = new HashMap<>();
                                                data = value.getData();

                                                Database.username = (String) data.get("username");
                                            }
                                        });

                                        Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                                        ManageUser.clear();
                                        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
//                                Log.i("User", "Logged in");
                                    } else
                                        Toast.makeText(LogInActivity.this, "Account not verified. Please check your email", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(LogInActivity.this, "Login failed! Please check your credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
            }break;

            case R.id.resetPasswordTextView:
            {
                try {
                    startActivity(new Intent(LogInActivity.this, ResetPasswordActivity.class));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }break;

            case R.id.signUpTextView:
            {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        }
    }
}