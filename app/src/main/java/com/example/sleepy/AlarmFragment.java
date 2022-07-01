package com.example.sleepy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    ArrayList<alarm_add> list = new ArrayList<>();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        FloatingActionButton buttonadd = view.findViewById(R.id.buttonadd);
        list.clear();

        recyclerView = view.findViewById(R.id.alarmlist);
        Log.e("alarmFragment", "called!");

        list = new ArrayList<>();
        Query query = database.collection(getusermail()).document("Alarm")
                .collection("alarms").orderBy("time",Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
        recyclerView = view.findViewById(R.id.alarmlist);
        myAdaptor = new AlarmAdaptor(view.getContext(), options, list);
        myAdaptor.notifyDataSetChanged();
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(myAdaptor);

        myAdaptor.setOnItemClickListener(new AlarmAdaptor.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(getContext(),UpdateAlarm.class);
                i.putExtra("alarmid", list.get(position).getAlarmID());
                Toast.makeText(getContext(), list.get(position).getAlarmID(), Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        });


        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAlarm.class);
                startActivity(intent);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // list.remove(viewHolder.getAdapterPosition());
                String alarmID = myAdaptor.getAlarmAt(viewHolder.getAdapterPosition()).getAlarmID();
                Log.e("swipe recycleview", "onSwiped: REMOVED " + viewHolder.getAdapterPosition() + " id : " + alarmID );

                CollectionReference itemref = database.collection(getusermail()).document("Alarm")
                        .collection("alarms");
                Query remove = itemref.whereEqualTo("alarmID", alarmID);
                remove.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.e("DELETE", ";;; ", task.getException());
                        if(task.isSuccessful()){
                            for(DocumentSnapshot snapshot : task.getResult()){
                                itemref.document(snapshot.getId()).delete();
                            }
                        }
                        else {
                            Log.e("DELETE", "Error getting documents: ", task.getException());
                        }
                    }
                });

                myAdaptor.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
        myAdaptor.notifyDataSetChanged();
        myAdaptor.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        myAdaptor.stopListening();
    }


    private String getusermail() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null) {
            return  user.getEmail();
        }
        return "ANONYMOUS" ;
    }

}