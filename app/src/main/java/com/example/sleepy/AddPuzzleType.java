package com.example.sleepy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPuzzleType extends AppCompatActivity {

    EditText puzzletype;
    Button btnpuzzle;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_puzzle_type);
        puzzletype = findViewById(R.id.tztPuzzletype);
        btnpuzzle = findViewById(R.id.btnPuzzleType);
        btnpuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PuzzleType");
                ref.push().setValue(puzzletype.getText().toString());
            }
        });
    }
}