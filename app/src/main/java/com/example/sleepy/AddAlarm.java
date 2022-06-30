package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AddAlarm extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference myRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button btn_sun,btn_mon,btn_tue,btn_wed,btn_thur,btn_fri,btn_sat;
    EditText txt_alarmname;
    int [] buttonclick = new int[]{0,0,0,0,0,0,0};
    int selectedindex;

    String[] spinnerobjects = { "Math" , "Diagram" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        Spinner spinnerpuzzle = findViewById(R.id.spinnerProblem);
        // ArrayList<String> listpuzzle = new ArrayList<>();

        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,spinnerobjects);
        //ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerpuzzle.setAdapter(ad);


        //buttoncolours();
        btn_sun = findViewById(R.id.btn_sun);
        btn_mon = findViewById(R.id.btn_mon);
        btn_tue = findViewById(R.id.btn_tue);
        btn_wed = findViewById(R.id.btn_wed);
        btn_thur = findViewById(R.id.btn_thur);
        btn_fri = findViewById(R.id.btn_fri);
        btn_sat = findViewById(R.id.btn_sat);

        btn_sun.setOnClickListener(this);
        btn_mon.setOnClickListener(this);
        btn_tue.setOnClickListener(this);
        btn_wed.setOnClickListener(this);
        btn_thur.setOnClickListener(this);
        btn_fri.setOnClickListener(this);
        btn_sat.setOnClickListener(this);


        Button btnadd = findViewById(R.id.btn_save);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePicker tp = (TimePicker) findViewById(R.id.createalarm_timePicker);
                String alarmTime = convertTime(tp.getCurrentHour()) + ":" + convertTime(tp.getCurrentMinute());

                finish();
                String uid = user.getUid().toString();

                String days = "";
                for(int i = 0; i< buttonclick.length; i++) {
                    if(buttonclick[i] != 0){
                        days += i;
                    }
                }

                //int PuzzleTypeID;
                String PuzzleType = spinnerpuzzle.getSelectedItem().toString();


                txt_alarmname = findViewById(R.id.txt_alarmname);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference key;
                key =  db.collection(getusermail()).document("Alarm").collection("alarms").document();
                String keyId = key.getId();

                alarm_add data = new alarm_add(keyId, alarmTime,uid,days,PuzzleType,txt_alarmname.getText().toString(),1);


                db.collection(getusermail()).document("Alarm").collection("alarms").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(AddAlarm.this,MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Alarm Created", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private String getusermail() {
        if(user != null) {
            return  user.getEmail();
        }
        return "ANONYMOUS" ;
    }


    public String convertTime(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }


    public String convertTimex(int input) {
        if (input >= 2 ) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }



    //days button code
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_sun) {
            buttonclick[0]++;
            if (buttonclick[0] == 1) {
                btn_sun.setBackgroundColor(Color.GREEN);
            } else {
                btn_sun.setBackgroundColor(Color.BLUE);
                buttonclick[0] = 0;
            }
        } else if (v.getId() == R.id.btn_mon) {
            buttonclick[1]++;
            if(buttonclick[1] == 1) {
                btn_mon.setBackgroundColor(Color.GREEN);
            } else {
                btn_mon.setBackgroundColor(Color.BLUE);
                buttonclick[1] = 0;
            }
        } else if (v.getId() == R.id.btn_tue) {
            buttonclick[2]++;
            if(buttonclick[2] == 1) {
                btn_tue.setBackgroundColor(Color.GREEN);
            } else {
                btn_tue.setBackgroundColor(Color.BLUE);
                buttonclick[2] = 0;
            }
        } else if (v.getId() == R.id.btn_wed) {
            buttonclick[3]++;
            if(buttonclick[3] == 1) {
                btn_wed.setBackgroundColor(Color.GREEN);
            } else {
                btn_wed.setBackgroundColor(Color.BLUE);
                buttonclick[3] = 0;
            }
        } else if (v.getId() == R.id.btn_thur) {
            buttonclick[4]++;
            if(buttonclick[4] == 1) {
                btn_thur.setBackgroundColor(Color.GREEN);
            } else {
                btn_thur.setBackgroundColor(Color.BLUE);
                buttonclick[4] = 0;
            }
        } else if (v.getId() == R.id.btn_fri) { buttonclick[5]++;
            if(buttonclick[5] == 1) {
                btn_fri.setBackgroundColor(Color.GREEN);
            } else {
                btn_fri.setBackgroundColor(Color.BLUE);
                buttonclick[5] = 0;
            }
        } else if (v.getId() == R.id.btn_sat) {
            buttonclick[6]++;
            if(buttonclick[6] == 1) {
                btn_sat.setBackgroundColor(Color.GREEN);
            } else {
                btn_sat.setBackgroundColor(Color.BLUE);
                buttonclick[6] = 0;
            }
        }


    }
}