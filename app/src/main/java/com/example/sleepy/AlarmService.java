package com.example.sleepy;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class AlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service", "service started");
        String emailAddr = intent.getStringExtra("EMAIL");
        Query query = FirebaseFirestore.getInstance().collection(emailAddr).document("Alarm")
                .collection("alarms").orderBy("time",Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    alarm_add alarm = queryDocumentSnapshot.toObject(alarm_add.class);
                    Log.e("svcAlarm", "Setting up alarm id: " + alarm.AlarmID);
                    String finalDays = alarm.getDays();

                    String[] timespilt = alarm.getTime().split(":");

                    Calendar calNow = Calendar.getInstance();
                    for (char c: finalDays.toCharArray()) {
                        Calendar calSet = (Calendar) calNow.clone();

                        calSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timespilt[0].trim()));
                        calSet.set(Calendar.MINUTE, Integer.parseInt(timespilt[1].trim()));
                        calSet.set(Calendar.SECOND, 0);
                        calSet.set(Calendar.MILLISECOND, 0);
                        calSet.set(Calendar.DAY_OF_WEEK, c - 47);
                        if (calSet.compareTo(calNow) <= 0) {
                            calSet.add(Calendar.DATE, 7);
                        }

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        intent.putExtra("passing_time", alarm.getTime());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) ((int) System.currentTimeMillis() + Math.random()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, (long) (calSet.getTimeInMillis() + Math.random()), (DateUtils.DAY_IN_MILLIS) * 7, pendingIntent);
                    }
                }
            }
        });

        //PULL ALL ALARMS FROM FIRESTORE AND THEN SET UP ALARMMANAGER AGAIN WHEN THE USER RESTARTS APP



        /*Calendar calNow = Calendar.getInstance();
        for (char c : days.toCharArray()) {
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hour);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND,0);
            calSet.set(Calendar.MILLISECOND,0);
            Log.e("day", String.valueOf(c - 47));
            calSet.set(Calendar.DAY_OF_WEEK, c - 47);
            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 7);
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.e("calSet TIME: ", String.valueOf(calSet.getTimeInMillis()));
            alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), (DateUtils.DAY_IN_MILLIS) * 7, pendingIntent);

            long[] pattern = {0, 2000, 5000, 2000, 5000, 2000, 5000};

            *//*NotificationCompat.Builder builder = new NotificationCompat.
                    Builder(getApplicationContext(),"Sleepy")
                    .setSmallIcon(R.drawable.sleepy)
                    .setContentTitle( alarmtime + " Alarm")
                    .setContentText("Its time for puzzles!!!")
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setFullScreenIntent(pendingIntent, true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVibrate(pattern);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(123,builder.build());*//*

            }*/


            Intent intent2 = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(),0,
                    intent2,PendingIntent.FLAG_UPDATE_CURRENT);

            Intent terminateIntent = new Intent(this, TerminateServiceReciver.class);
            PendingIntent terminatePendingIntent = PendingIntent.getBroadcast(this, 0, terminateIntent, 0);

                Notification notification = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    notification = new Notification.Builder(this, "Sleepy")
                            .setSmallIcon(R.drawable.sleepy)
                            .setContentTitle("Alarm")
                            .setContentText("Waiting for next alarm")
                            .setAutoCancel(true)
                            .setOngoing(true)
                            .setFullScreenIntent(pendingIntent2, true)
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
                            .setTicker("Sleepy")
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setGroup("SLEEPY")
                            .addAction(R.drawable.sleepy ,"Terminate", terminatePendingIntent)
                            .build();
                     Log.e("Notification", notification.toString());


                }
                else {
                    notification = new Notification.Builder(this, "Sleepy")
                            .setSmallIcon(R.drawable.sleepy)
                            .setContentTitle("Alarm")
                            .setContentText("Waiting for next alarm!!!")
                            .setAutoCancel(true)
                            .setOngoing(true)
                            .setFullScreenIntent(pendingIntent2, true)
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setTicker("Sleepy")
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setGroup("SLEEPY")
                            .addAction(R.drawable.sleepy ,"Terminate", terminatePendingIntent)
                            .build();
                }

        startForeground(1, notification);
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service terminated", "terminated");
        stopForeground(true);
    }
}
