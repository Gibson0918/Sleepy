package com.example.sleepy;

import android.os.Bundle;

import androidx.annotation.BoolRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    RecyclerView recyclerView;
    ArrayList<History> list = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mFirebaseAuth.getCurrentUser();
    HistoryAdapter arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        list.clear();
        Query query = db.collection(getusermail()).document("History")
                .collection("history");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    History history = queryDocumentSnapshot.toObject(History.class);
                    list.add(history);
                    Log.e("query", history.toString() );
                    Log.e("list", String.valueOf(list));
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        FirestoreRecyclerOptions<History> options = new FirestoreRecyclerOptions.Builder<History>()
                .setQuery(query, History.class)
                .build();
        recyclerView = view.findViewById(R.id.recyclehistory);
        arrayAdapter = new HistoryAdapter(view.getContext(), options, list);
        Log.e("list2", String.valueOf(list));
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        return view;
    }


    private String getusermail() {
        if(user != null) {
            Log.e("historyuser", user.getEmail() );
            return  user.getEmail();
        }
        return "ANONYMOUS" ;
    }


    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getRecycledViewPool().clear();
        arrayAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        arrayAdapter.stopListening();
    }

}