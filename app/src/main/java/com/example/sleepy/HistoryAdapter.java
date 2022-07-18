package com.example.sleepy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class HistoryAdapter extends FirestoreRecyclerAdapter<History,HistoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<History> list;


    public HistoryAdapter(Context context , @NonNull FirestoreRecyclerOptions<History> options , ArrayList<History> histories) {
        super(options);
        this.list = histories;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull History model) {
        holder.histories.setText(model.getDate());
        if(model.getPass().equals("yes")) {
            holder.card.setCardBackgroundColor(Color.GREEN);
        } else {
            holder.card.setCardBackgroundColor(Color.RED);
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.historycard,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView histories;
        CardView card;
        public MyViewHolder(View itemview) {
            super(itemview);
            histories = itemview.findViewById(R.id.txthistories);
            card = itemview.findViewById(R.id.historycard);
        }
    }
}
