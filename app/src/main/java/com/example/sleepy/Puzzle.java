package com.example.sleepy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Puzzle extends AppCompatActivity {

    TextView txtques;
    EditText editans;
    Button btndismiss;
    double ans = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    SharedPreferences sharedpreferences;
    public static final String Email = "emailKey";// creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        txtques = findViewById(R.id.txtquestion);
        editans = findViewById(R.id.editTextAnswer);
        btndismiss = findViewById(R.id.btnSnooze);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        email = sharedpreferences.getString(Email, null);
        Log.e("email",email );

        int min = 0;
        int max = 100;
        int minswitch = 1;
        int maxswitch = 4;

        //Generate random int value from 50 to 100
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        int random_int2 = (int)Math.floor(Math.random()*(max-min+1)+min);
        int random_switch = (int)Math.floor(Math.random()*(maxswitch-minswitch+1)+minswitch);

        switch(random_switch) {
            case 1:
                ans = random_int + random_int2;
                txtques.setText(random_int + " + " + random_int2);
            case 2:
                ans = random_int - random_int2;
                txtques.setText(random_int + " - " + random_int2);
                break;
            case 3:
                ans = random_int * random_int2;
                txtques.setText(random_int + " x " + random_int2);
                break;
            case 4:
                ans = (double)random_int / random_int2;
                txtques.setText(random_int + " / " + random_int2);
        }

        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double input = Double.parseDouble(editans.getText().toString());
                Calendar calNow = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
               // SimpleDateFormat sdf2 = new SimpleDateFormat("HH:MM");
                String date = sdf.format(calNow.getTime()).toString();
               // String time = sdf2.format(calNow.getTime()).toString();
                ans = Math.abs(ans);
                if (input == ans) {
                    History history = new History(date, "yes");
                    db.collection(email).document("History").collection("history").add(history)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent = new Intent(Puzzle.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                } else {

                    History history = new History(date , "No");
                    db.collection(email).document("History").collection("history").add(history)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(Puzzle.this, "Wrong Answer.",
                                            Toast.LENGTH_SHORT).show();
                                    editans.getText().clear();
                                }
                            });
                }
            }
        });

    }
}