package com.matthewcampisi.selfi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity {

    private String displayName;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser user;
    private TextView greetingTextValue;
    private Button signOutBtn, healthBtn, scanBtn;
    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        userData();
        greetingTextValue = findViewById(R.id.menu_greetings);
        signOutBtn = findViewById(R.id.menu_sign_out_btn);
        healthBtn = findViewById(R.id.menu_health_btn);
        scanBtn = findViewById(R.id.menu_scan_btn);
        layout = findViewById(R.id.menu_linear_layout);

        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();

        if(extras != null) {
            if (extras.containsKey("login")) {
                Snackbar.make(layout,
                        extras.getString("login"),
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (extras.containsKey("registration")) {
                Snackbar.make(layout,
                        extras.getString("registration"),
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sign out", "Signed out");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        healthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HealthActivity.class);
                startActivity(intent);
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, BluetoothDevicesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userData(){
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
        user=FirebaseAuth.getInstance().getCurrentUser();
        String userid=user.getUid();
        userRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayName = dataSnapshot.child("userDisplayName").getValue(String.class);
                greetingTextValue.setText("Hi " + displayName);
                Log.d("name",displayName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
}
