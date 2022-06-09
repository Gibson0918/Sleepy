package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddQuestions extends AppCompatActivity {

    EditText ques,ans;
    Button addques;
    DatabaseReference myRef;
    String[] spinnerobjects = { "Math" , "Diagram" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        ques = findViewById(R.id.txtques);
        ans = findViewById(R.id.txtans);
        addques = findViewById(R.id.btnques);

        Spinner spinnerpuzzletype = findViewById(R.id.spinner_addquestype);

         DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("PuzzleType");
         ArrayList<String> listpuzzle = new ArrayList<>();

        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,listpuzzle);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listpuzzle.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    listpuzzle.add(snapshot.getValue().toString());
                    ad.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

       // ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,spinnerobjects);
        //ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerpuzzletype.setAdapter(ad);

        addques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PuzzleType = spinnerpuzzletype.getSelectedItem().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tasks");
                String key = ref.push().getKey();
                Question data = new Question(ques.getText().toString(),ans.getText().toString(), PuzzleType, key);
                ref.push().setValue(data);
            }
        });
    }
}