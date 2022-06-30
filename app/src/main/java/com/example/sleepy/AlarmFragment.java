package com.example.sleepy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlarmFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    AlarmAdaptor myAdaptor;
    ArrayList<alarm_add> list;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        FloatingActionButton buttonadd = view.findViewById(R.id.buttonadd);

        recyclerView = view.findViewById(R.id.alarmlist);

        list = new ArrayList<>();
        Query query = database.collection(getusermail()).document("Alarm")
                .collection("alarms").orderBy("time",Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    alarm_add alarms = queryDocumentSnapshot.toObject(alarm_add.class);
                    list.add(alarms);
                    myAdaptor.notifyDataSetChanged();
                }
            }
        });

        FirestoreRecyclerOptions<alarm_add> options = new FirestoreRecyclerOptions.Builder<alarm_add>()
                .setQuery(query, alarm_add.class)
                .build();
        //setup recycle view
        myAdaptor = new AlarmAdaptor(view.getContext(), options, list);
        myAdaptor.notifyDataSetChanged();
       // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        //myAdaptor.notifyDataSetChanged();
        recyclerView.setAdapter(myAdaptor);

        myAdaptor.setOnItemClickListener(new AlarmAdaptor.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), list.get(position).getTime(), Toast.LENGTH_SHORT).show();
                // Toast.makeText(getContext(), keys.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAlarm.class);
                getActivity().startActivity(intent);
                //getActivity().finish();
            }
        });
        return view;
    }

    private String getusermail() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null) {
            return  user.getEmail();
        }
        return "ANONYMOUS" ;
    }

    @Override
    public void onStart() {
        super.onStart();
        myAdaptor.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        myAdaptor.stopListening();
    }
}