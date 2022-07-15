package com.example.sleepy;

import static android.os.VibrationAttributes.USAGE_ALARM;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("alarm", "received");
        //notification code later
       Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            long[] pattern = {0, 2000, 5000, 2000, 5000, 2000, 5000};
            vibrator.vibrate(pattern, 8);
        }
        else {
            vibrator.vibrate(10000);
        }
        // Output yes if can vibrate, no otherwise
            if (vibrator.hasVibrator()) {
                Log.e("Can Vibrate", "YES");
            } else {
                Log.e("Can Vibrate", "NO");
            }
        String alarmtime = intent.getStringExtra("passing_time");
        Intent i = new Intent(context,Snooze.class);
        i.putExtra("alarmtiming", alarmtime);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,
                i,PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {0, 2000, 5000, 2000, 5000, 2000, 5000};

        NotificationCompat.Builder builder = new NotificationCompat.
                Builder(context,"Sleepy")
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
                .setGroup("SLEEPY")
                .setVibrate(pattern);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify((int) System.currentTimeMillis(),builder.build());
    }
}
