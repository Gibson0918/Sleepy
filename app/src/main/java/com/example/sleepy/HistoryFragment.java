package com.example.sleepy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    ListView listview;
    ArrayList<History> histories = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mFirebaseAuth.getCurrentUser();
    ArrayAdapter arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        listview = view.findViewById(R.id.listhistory);
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                histories);
        listview.setAdapter(arrayAdapter);
        // store data
        Query query = db.collection(getusermail()).document("History")
                .collection("history").orderBy("date",Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    History history = queryDocumentSnapshot.toObject(History.class);
                    histories.add(history);
                    Log.e("query", histories.toString() );

                }
                arrayAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }


    private String getusermail() {
        if(user != null) {
            return  user.getEmail();
        }
        return "ANONYMOUS" ;
    }

}