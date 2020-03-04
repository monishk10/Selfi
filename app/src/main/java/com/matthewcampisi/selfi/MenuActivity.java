package com.matthewcampisi.selfi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        userData();
        greetingTextValue = findViewById(R.id.greetings);
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
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void scanDevices(View view) {
        Intent intent = new Intent(this, BluetoothDevicesActivity.class);
        startActivity(intent);
    }

    public void openHealth(View view) {
        Intent intent = new Intent(this, HealthActivity.class);
        startActivity(intent);
    }
}
