package com.matthewcampisi.selfi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText displayTextView, emailTextView, passwordTextView, confirmPasswordTextView;
    private TextView loginTextView;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ConstraintLayout layout;
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%]).{6,20})";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // taking Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        // initializing all views
        displayTextView = findViewById(R.id.r_name);
        emailTextView = findViewById(R.id.r_email);
        passwordTextView = findViewById(R.id.r_password);
        confirmPasswordTextView = findViewById(R.id.r_confirm_password);
        loginTextView = findViewById(R.id.r_login_btn);
        registerButton = findViewById(R.id.r_register_btn);
        progressBar = findViewById(R.id.r_progress_bar);
        layout = findViewById(R.id.r_constraint_layout);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerNewUser() {


        // Take the value of two edit texts in Strings
        final String displayName, email, password, confirmPassword;
        displayName = displayTextView.getText().toString();
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        confirmPassword = confirmPasswordTextView.getText().toString();

        // Validations for input email and password


        if (TextUtils.isEmpty(displayName)) {
            Snackbar.make(layout,
                    "Please enter a name!!",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(layout,
                    "Please enter email!!",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        } else {
            if(!email.trim().matches(EMAIL_PATTERN)) {
                Snackbar.make(layout,
                        "Invalid email address",
                        Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Snackbar.make(layout,
                    "Please enter password!!",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        } else if (!password.trim().matches(PASSWORD_PATTERN)) {
            Snackbar.make(layout,
                    "Please enter valid password!!",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        } else if (!password.equals(confirmPassword)) {
            Snackbar.make(layout,
                    "Both password are not equal!!",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        // show the progress bar visibility
        progressBar.setVisibility(View.VISIBLE);

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            User user = new User(
                                    displayName,
                                    email
                            );

                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("Users");
                            myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        // if the user created intent to login activity
                                        Intent intent = new Intent(RegistrationActivity.this, MenuActivity.class);
                                        //Create the bundle
                                        Bundle bundle = new Bundle();

                                        //Add your data to bundle
                                        bundle.putString("registration", "Registration successful!!");

                                        //Add the bundle to the intent
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    } else {
                                        // User failed
                                        Snackbar.make(layout,
                                                "User detail entry failed!!"
                                                        + " Please try again later",
                                                Snackbar.LENGTH_SHORT)
                                                .show();

                                    }
                                }
                            });

                        }
                        else {

                            // Registration failed
                            Snackbar.make(layout,
                                    "Registration failed!!"
                                            + " Please try again later",
                                    Snackbar.LENGTH_SHORT)
                                    .show();

                            // hide the progress bar
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}
