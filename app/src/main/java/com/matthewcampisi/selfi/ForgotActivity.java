package com.matthewcampisi.selfi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button resetButton;
    private EditText emailEditText;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        resetButton = findViewById(R.id.f_forgot_btn);
        emailEditText = findViewById(R.id.f_email);
        loginTextView = findViewById(R.id.f_login_btn);
        mAuth = FirebaseAuth.getInstance();

        // Set on Click Listener on Login button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                resetEmail();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            //TODO: think of something
        } else {

        }
    }

    private void resetEmail(){
        String email;
        email = emailEditText.getText().toString();

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("forgot", "Email sent.");
                        Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("forgot", "Reset link sent successfully!!");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
    }
}
