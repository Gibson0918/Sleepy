package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.sleepy.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String CHANNEL_ID = "Sleepy";
    BottomNavigationView bottomNavigationView;
    AlarmFragment alarmFragment = new AlarmFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.alarmtab);
        createNotificationChannel();
        Intent alarmSvcIntent = new Intent(this, AlarmService.class);
        alarmSvcIntent.putExtra("EMAIL", getUseremail());
        startForegroundService(alarmSvcIntent);
    }

    private String getUseremail() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null) {
        return  user.getEmail();
        }
        return "ANONYMOUS" ;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Fragment selected = null;
        switch (item.getItemId()) {
            case R.id.alarmtab:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, alarmFragment).commit();
                return true;

            case R.id.historytab:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, historyFragment).commit();
                return true;

            case R.id.settingtab:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportFragmentManager().beginTransaction().detach(alarmFragment).commitNow();
        getSupportFragmentManager().beginTransaction().attach(alarmFragment).commitNow();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}