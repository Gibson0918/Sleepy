package com.example.sleepy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AlarmFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    AlarmAdaptor myAdaptor;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        FloatingActionButton buttonadd = view.findViewById(R.id.buttonadd);

        recyclerView = view.findViewById(R.id.alarmlist);
        Log.e("alarmFragment", "called!");

        Query query = database.collection(getusermail()).document("Alarm")
                .collection("alarms").orderBy("time",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<alarm_add> options = new FirestoreRecyclerOptions.Builder<alarm_add>()
                .setQuery(query, alarm_add.class)
                .build();
        //setup recycle view
        myAdaptor = new AlarmAdaptor(view.getContext(), options);
        //myAdaptor.notifyDataSetChanged();
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(myAdaptor);

        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAlarm.class);
                startActivity(intent);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // list.remove(viewHolder.getAdapterPosition());
                String docID = myAdaptor.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getId();
                Log.e("swipe recycleview", "onSwiped: REMOVED " + viewHolder.getAdapterPosition() + " id : " + docID );
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                database.collection(getusermail()).document("Alarm").collection("alarms").document(docID).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        String count = String.valueOf(documentSnapshot.getLong("taskID").intValue());
                                        String days = documentSnapshot.get("days").toString();
                                        Log.e("COUNT123", count);

                                        for (char c : days.toCharArray()) {
                                            String countStr = count + String.valueOf(c);
                                            int newCount1 = Integer.parseInt(countStr);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), newCount1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            if (alarmManager != null) {
                                                Log.e("ALARM 123", "cancelled");
                                                alarmManager.cancel(pendingIntent);

                                            }
                                            Log.e("turn off alarm: ", String.valueOf(newCount1) + "OFF");
                                        }

                                        database.collection(getusermail()).document("Alarm").collection("alarms").document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.e("delete: ", "SUCCESS");
                                                myAdaptor.notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("delete: ", "FAILURE");
                                            }
                                        });

                                    } else {
                                        Log.e("COUNT123: ", "DNE" );
                                    }
                                }
                            }
                });
            }
        }).attachToRecyclerView(recyclerView);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
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