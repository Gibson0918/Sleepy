package com.example.sleepy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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

    BottomNavigationView bottomNavigationView;
    AlarmFragment alarmFragment = new AlarmFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.alarmtab);
        //startService(new Intent(getApplicationContext(),MyService.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selected = null;
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
}