package com.example.sleepy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AlarmAdaptor extends RecyclerView.Adapter<AlarmAdaptor.MyViewHolder> {

    Context context;
    ArrayList<alarm_add> list;
    private onItemClickListener mListener;

    public AlarmAdaptor(Context context, ArrayList<alarm_add> alarms) {
        this.context = context;
        this.list = alarms;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = (onItemClickListener) listener;
    }

    @NonNull
    @Override
    public AlarmAdaptor.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.alarm,parent,false);
        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        alarm_add alarms = list.get(position);
        holder.txttime.setText(alarms.getTime());
        if(alarms.getIsup() == 1) {
            holder.tg_alarmon.setChecked(true);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
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
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
