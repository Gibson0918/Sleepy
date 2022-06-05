package com.example.sleepy;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddAlarm extends AppCompatActivity {

    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        Spinner spinnerpuzzle = findViewById(R.id.spinnerProblem);
        ArrayList<String> listpuzzle = new ArrayList<>();


        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,listpuzzle);
        //ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerpuzzle.setAdapter(ad);


        //DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("PuzzleType");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("PuzzleType");
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

        buttoncolours();

        Button btnadd = findViewById(R.id.btn_save);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePicker tp = (TimePicker) findViewById(R.id.createalarm_timePicker);
                String alarmTime = tp.getCurrentHour() + ":" + tp.getCurrentMinute();
                Toast.makeText(getApplicationContext(),alarmTime, Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(AddAlarm.this,MainActivity.class);
                startActivity(intent);
            }
        });

        Button btncancel = findViewById(R.id.btn_cancel);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAlarm.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }



    private void buttoncolours() {


        final Button btn_sun = findViewById(R.id.btn_sun);
        btn_sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) btn_sun.getBackground();
                int colorId = buttonColor.getColor();
                if (colorId != Color.BLUE) {
                    btn_sun.setBackgroundColor(Color.BLUE);
                } else {
                //    btn_sun.getBackground().clearColorFilter();
                    //https://www.youtube.com/watch?v=xWWnrh_Gks0
                }
            }
        });


        final Button btn_mon = findViewById(R.id.btn_mon);
        btn_mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_mon.setBackgroundColor(Color.BLUE);
            }
        });

        final Button btn_tue = findViewById(R.id.btn_tue);
        btn_tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_tue.setBackgroundColor(Color.BLUE);
            }
        });

        final Button btn_wed = findViewById(R.id.btn_wed);
        btn_wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_wed.setBackgroundColor(Color.BLUE);
            }
        });

        final Button btn_thur = findViewById(R.id.btn_thur);
        btn_thur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_thur.setBackgroundColor(Color.BLUE);
            }
        });

        final Button btn_fri = findViewById(R.id.btn_fri);
        btn_fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_fri.setBackgroundColor(Color.BLUE);
            }
        });


        final Button btn_sat = findViewById(R.id.btn_sat);
        btn_sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_sat.setBackgroundColor(Color.BLUE);
            }
        });


        final Button btn_snooze = findViewById(R.id.btn_snooze);
        btn_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_snooze.setBackgroundColor(Color.BLUE);
            }
        });

        final Button btn_repeat = findViewById(R.id.btn_repeat);
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_repeat.setBackgroundColor(Color.BLUE);
            }
        });

        //end buttoncolours method
    }
}