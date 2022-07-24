package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UpdateAlarm extends AppCompatActivity implements View.OnClickListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mFirebaseAuth.getCurrentUser();
    EditText txtalarmlable;
    TimePicker alarmpicker;
    Button btn_sun,btn_mon,btn_tue,btn_wed,btn_thur,btn_fri,btn_sat;
    Button btnadd;
    Button btncancel;
    Button btnsave;
    Spinner spinnerset;
    String[] spinnerobjects = { "Math" , "Diagram" };
    int [] buttonclick = new int[]{0,0,0,0,0,0,0};
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_alarm);
        Intent intent = getIntent();
        String alarmID = intent.getStringExtra("alarmid");
        Log.e("Update alarm class", alarmID + " ID : " + getusermail() );

        txtalarmlable = findViewById(R.id.txtalarmlable);
        btn_sun = findViewById(R.id.btn_sun);
        btn_mon = findViewById(R.id.btn_mon);
        btn_tue = findViewById(R.id.btn_tue);
        btn_wed = findViewById(R.id.btn_wed);
        btn_thur = findViewById(R.id.btn_thur);
        btn_fri = findViewById(R.id.btn_fri);
        btn_sat = findViewById(R.id.btn_sat);
        spinnerset = findViewById(R.id.spinnerset);
        ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,spinnerobjects);
        spinnerset.setAdapter(ad);
        btnadd = findViewById(R.id.btn_save);
        btncancel = findViewById(R.id.btn_cancel);
        btnsave = findViewById(R.id.btn_save);
        final TimePicker tp = (TimePicker) findViewById(R.id.createalarm_timePicker);

        btn_sun.setOnClickListener(this);
        btn_mon.setOnClickListener(this);
        btn_tue.setOnClickListener(this);
        btn_wed.setOnClickListener(this);
        btn_thur.setOnClickListener(this);
        btn_fri.setOnClickListener(this);
        btn_sat.setOnClickListener(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference countRef = db.collection(getusermail()).document("Counter");
        countRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    count = documentSnapshot.getLong("count").intValue();
                }
            }
        });



        db.collection(getusermail()).document("Alarm")
                .collection("alarms").whereEqualTo("alarmID", alarmID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                alarm_add alarms = queryDocumentSnapshot.toObject(alarm_add.class);
                                if(alarms.getLabel() != null){
                                    txtalarmlable.setText(alarms.getLabel());
                                }

                                if(alarms.getPuzzleType().equals("Math")){
                                    spinnerset.setSelection(0);
                                } else {
                                    spinnerset.setSelection(1);
                                }

                                //set time picker time from db
                                String oldtime = alarms.getTime();
                                String hr = oldtime.substring(0,2);
                                String min = oldtime.substring(3,5);
                                Log.e("time", hr );
                                Log.e("time", min );
                                tp.setHour(Integer.valueOf(hr));
                                tp.setMinute(Integer.valueOf(min));



                                        String days = alarms.getDays();
                                String [] listdays = days.split("");
                                for(int i =0; i< listdays.length; i++){
                                    if(listdays[i].equals("0")){
                                        btn_sun.setBackgroundColor(Color.GREEN);
                                        buttonclick[0] = 1;
                                    }
                                    if(listdays[i].equals("1")){
                                        btn_mon.setBackgroundColor(Color.GREEN);
                                        buttonclick[1] = 1;
                                    }
                                    if(listdays[i].equals("2")){
                                        btn_tue.setBackgroundColor(Color.GREEN);
                                        buttonclick[2] = 1;
                                    }
                                    if(listdays[i].equals("3")){
                                        btn_wed.setBackgroundColor(Color.GREEN);
                                        buttonclick[3] = 1;
                                    }
                                    if(listdays[i].equals("4")){
                                        btn_thur.setBackgroundColor(Color.GREEN);
                                        buttonclick[4] = 1;
                                    }
                                    if(listdays[i].equals("5")){
                                        btn_fri.setBackgroundColor(Color.GREEN);
                                        buttonclick[5] = 1;
                                    }
                                    if(listdays[i].equals("6")){
                                        btn_sat.setBackgroundColor(Color.GREEN);
                                        buttonclick[6] = 1;
                                    }
                                }


                            }
                        }
                    }
                });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateAlarm.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PuzzleType = spinnerset.getSelectedItem().toString();
                String days = "";
                for(int i = 0; i< buttonclick.length; i++) {
                    if(buttonclick[i] != 0){
                        days += i;
                    }
                }

                String alarmTime = convertTime(tp.getCurrentHour()) + ":" + convertTime(tp.getCurrentMinute());
                alarm_add data = new alarm_add(alarmID, alarmTime,user.getUid(),days,PuzzleType,txtalarmlable.getText().toString(),1, count);


                CollectionReference itemref = db.collection(getusermail()).document("Alarm")
                        .collection("alarms");
                Query update = itemref.whereEqualTo("alarmID", alarmID);
                update.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot snapshot : task.getResult()) {
                                itemref.document(snapshot.getId()).update("days", data.getDays());
                                itemref.document(snapshot.getId()).update("label", data.getLabel());
                                itemref.document(snapshot.getId()).update("puzzleType", data.getPuzzleType());
                                itemref.document(snapshot.getId()).update("time", data.getTime());
                            }
                        }
                    }
                });

                Intent intent = new Intent(UpdateAlarm.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Alarm Updated", Toast.LENGTH_SHORT).show();
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