package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddAlarm extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference myRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button btn_sun,btn_mon,btn_tue,btn_wed,btn_thur,btn_fri,btn_sat;
    EditText txt_alarmname;
    int [] buttonclick = new int[]{0,0,0,0,0,0,0};
    int selectedindex;
    int count;

    String[] spinnerobjects = { "Math" , "Diagram" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        Spinner spinnerpuzzle = findViewById(R.id.spinnerset);
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

                txt_alarmname = findViewById(R.id.txtalarmlable);

                String[] timespilt = alarmTime.split(":");
                Log.d("hour", timespilt[0]);
                Log.d("min", timespilt[1]);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference key;
                key =  db.collection(getusermail()).document("Alarm").collection("alarms").document();
                String keyId = key.getId();
                String finalDays = days;

                DocumentReference countRef = db.collection(getusermail()).document("Counter");
                countRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            count = documentSnapshot.getLong("count").intValue();
                            countRef.update("count", ++count);
                            Log.e("countRef", String.valueOf(count));
                            alarm_add data = new alarm_add(keyId, alarmTime, uid, finalDays, PuzzleType, txt_alarmname.getText().toString(), 1, count);
                            db.collection(getusermail()).document("Alarm").collection("alarms").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Calendar calNow = Calendar.getInstance();
                                    for (char c: finalDays.toCharArray()) {
                                        Calendar calSet = (Calendar) calNow.clone();

                                        calSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timespilt[0].trim()));
                                        calSet.set(Calendar.MINUTE, Integer.parseInt(timespilt[1].trim()));
                                        calSet.set(Calendar.SECOND,0);
                                        calSet.set(Calendar.MILLISECOND,0);
                                        Log.e("day", String.valueOf(c - 47));
                                        calSet.set(Calendar.DAY_OF_WEEK, c - 47);
                                        if (calSet.compareTo(calNow) <= 0) {
                                            calSet.add(Calendar.DATE, 7);
                                        }

                                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                        Intent intent = new Intent(AddAlarm.this, AlarmReceiver.class);
                                        intent.putExtra("passing_time", alarmTime);
                                        String countStr = count + String.valueOf(c);
                                        int newCount1 = Integer.parseInt(countStr);
                                        Log.e("newCount: ", String.valueOf(newCount1));
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), newCount1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        Log.e("calSet TIME: ", String.valueOf(calSet.getTimeInMillis()));
                                        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
                                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), (DateUtils.DAY_IN_MILLIS) * 7, pendingIntent);
                                    }
                                    Toast.makeText(getApplicationContext(), "Alarm Created", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Alarm Creation Failed", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Alarm Creation Failed", Toast.LENGTH_SHORT).show();
                        finish();
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