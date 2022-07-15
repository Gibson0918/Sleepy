package com.example.sleepy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Snooze extends AppCompatActivity {
    Button btnSnooze;
    TextView alarmTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        btnSnooze = findViewById(R.id.btnSnooze);
        alarmTv = findViewById(R.id.txtAlarmtime);
        String alarmTime = getIntent().getStringExtra("alarmtiming");
        alarmTv.setText(alarmTime);


        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Snooze.this, Puzzle.class);
                startActivity(intent);
                finish();
            }
        });
    }
}