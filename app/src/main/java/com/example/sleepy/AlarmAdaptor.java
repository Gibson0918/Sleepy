package com.example.sleepy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class AlarmAdaptor extends FirestoreRecyclerAdapter<alarm_add,AlarmAdaptor.MyViewHolder> {

    //https://stackoverflow.com/questions/49277797/how-to-display-data-from-firestore-in-a-recyclerview-with-android/49277842

    Context context;
    private onItemClickListener mListener;
    ArrayList<alarm_add> list = new ArrayList<>();
    //private RecyclerView.ViewHolder holder;

    public AlarmAdaptor(Context context, @NonNull FirestoreRecyclerOptions<alarm_add> options, ArrayList<alarm_add> list) {
        super(options);
        this.context = context;
        this.list = list;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = (onItemClickListener) listener;
    }

    @Override
    public AlarmAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm,parent,false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull alarm_add model) {
        int safeposition = holder.getLayoutPosition();
        alarm_add alarms = list.get(safeposition);
        holder.txttime.setText(model.getTime());
       if(alarms.getIsup() == 1) {
            holder.tg_alarmon.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txttime;
        ToggleButton tg_alarmon;
        public MyViewHolder(View itemview , final onItemClickListener listener) {
            super(itemview);
            txttime = itemview.findViewById(R.id.txtTime);
            tg_alarmon = itemview.findViewById(R.id.tg_alarmon);
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getLayoutPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            long key = getItemId();
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}