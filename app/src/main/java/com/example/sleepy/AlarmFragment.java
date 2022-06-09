package com.example.sleepy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference database;
    AlarmAdaptor myAdaptor;
    ArrayList<alarm_add> list;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        FloatingActionButton buttonadd = view.findViewById(R.id.buttonadd);

        recyclerView = view.findViewById(R.id.alarmlist);
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference().child("Alarms");
        Query query = database.orderByChild("userID").equalTo(user.getUid());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myAdaptor = new AlarmAdaptor(getContext(),list);
        recyclerView.setAdapter(myAdaptor);

        ArrayList<String> keys = new ArrayList<>();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    alarm_add alarms = dataSnapshot.getValue(alarm_add.class);
                    list.add(alarms);
                    keys.add(snapshot.getKey());
                }
                myAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myAdaptor.setOnItemClickListener(new AlarmAdaptor.onItemClickListener() {
        @Override
        public void onItemClick(int position) {
           Toast.makeText(getContext(),list.get(position).getTime(), Toast.LENGTH_SHORT).show();
           // Toast.makeText(getContext(), keys.get(position), Toast.LENGTH_SHORT).show();
        }
        });






        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddAlarm.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }




}