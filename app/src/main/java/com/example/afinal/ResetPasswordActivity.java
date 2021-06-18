package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText emailField;
    Button button;
    String email;
    DisplayMetrics displayMetrics;

    Database database = Database.getInstance();
    FirebaseAuth mAuth = database.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        bindWidgets();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*0.7), (int) (height*0.5));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailField.getText().toString();
                Log.i("email", email);
                sendVerificationEmail();
            }
        });
    }

    private void bindWidgets()
    {
        button = (Button) findViewById(R.id.buttonResetPassword);
        emailField = (EditText) findViewById(R.id.editTextResetPassword);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAfterTransition();
    }

    private void sendVerificationEmail()
    {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailField.setError("Invalid email");
            emailField.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(ResetPasswordActivity.this, "Password reset link sent", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ResetPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}