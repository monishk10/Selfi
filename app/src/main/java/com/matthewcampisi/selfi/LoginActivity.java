package com.matthewcampisi.selfi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private TextView forgotPasswordTextView, registerTextView;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ConstraintLayout layout;
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // taking instance of Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Bundle extras= getIntent().getExtras();

        //initialization
        emailTextView = findViewById(R.id.l_email);
        forgotPasswordTextView = findViewById(R.id.l_forgot_btn);
        registerTextView = findViewById(R.id.l_register_btn);
        passwordTextView = findViewById(R.id.l_password);
        loginButton = findViewById(R.id.l_login_btn);
        progressBar = findViewById(R.id.l_progress_bar);
        layout = findViewById(R.id.l_constraint_layout);

        if(extras != null) {
            if(extras.containsKey("sign out")) {
                Snackbar.make(layout,
                        extras.getString("sign out"),
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if(extras.containsKey("forgot")) {
                Snackbar.make(layout,
                        extras.getString("forgot"),
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }

        // Set on Click Listener on Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUserAccount();
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUserAccount()
    {



        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(layout,
                    "Please Enter email",
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

        if (TextUtils.isEmpty(password)) {
            Snackbar.make(layout,
                    "Please enter password!!",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        // show the visibility of progress bar to show loading
        progressBar.setVisibility(View.VISIBLE);

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    // if sign-in is successful
                                    // intent to home activity
                                    Intent intent = new Intent(LoginActivity.this,
                                                                            MenuActivity.class);

                                    //Create the bundle
                                    Bundle bundle = new Bundle();

                                    //Add your data to bundle
                                    bundle.putString("login", "Login successful!!");

                                    //Add the bundle to the intent
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }

                                else {

                                    // sign-in failed
                                    Snackbar.make(layout,
                                            "Incorrect email/password!!!",
                                            Snackbar.LENGTH_SHORT)
                                            .show();


                                }
                                // hide the progress bar
                                progressBar.setVisibility(View.GONE);
                            }
                        });
    }


}
