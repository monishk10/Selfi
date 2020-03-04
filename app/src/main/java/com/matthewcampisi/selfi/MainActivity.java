package com.matthewcampisi.selfi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Initialization of instances
    private Handler mHandler = new Handler();
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if any user is signed-in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in then take directly to the main activity
            intent = new Intent(this, MenuActivity.class);
        } else {
            // Take it to the login page
            intent = new Intent(this, LoginActivity.class);
        }

        // Run the animation for 1 second
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 1000); // 1 second


    }
}
