package com.example.sleepy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmAdaptor extends FirestoreRecyclerAdapter<alarm_add,AlarmAdaptor.MyViewHolder> {

    Context context;
    private onItemClickListener mListener;
    ArrayList<alarm_add> list;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    
    public AlarmAdaptor(Context context , @NonNull FirestoreRecyclerOptions<alarm_add> options , ArrayList<alarm_add> alarms) {
        super(options);
        this.context = context;
        this.list = alarms;
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
        //alarm_add alarms = list.get(safeposition);
        holder.txttime.setText(model.getTime());

        if(model.getIsup() == 1) {
            holder.tg_alarmon.setChecked(true);
        } else {
            holder.tg_alarmon.setChecked(false);
        }

        holder.tg_alarmon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String alarmID = list.get(safeposition).getAlarmID();
                String time = list.get(safeposition).getTime();
                Integer taskID = list.get(safeposition).getTaskID();
                String days = list.get(safeposition).getDays();

                Log.e("UPDATE", alarmID);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                CollectionReference itemref = database.collection(getusermail()).document("Alarm")
                        .collection("alarms");
                Query update = itemref.whereEqualTo("alarmID", alarmID);
                update.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot snapshot : task.getResult()) {
                                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(context, AlarmReceiver.class);
                                intent.putExtra("passing_time", time);
                                if(isChecked){
                                    itemref.document(snapshot.getId()).update("isup", 1);
                                    Calendar calNow = Calendar.getInstance();
                                    String[] timespilt = time.split(":");

                                    for (char c: days.toCharArray()) {
                                        Calendar calSet = (Calendar) calNow.clone();

                                        calSet.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timespilt[0].trim()));
                                        calSet.set(Calendar.MINUTE, Integer.parseInt(timespilt[1].trim()));
                                        calSet.set(Calendar.SECOND, 0);
                                        calSet.set(Calendar.MILLISECOND, 0);
                                        Log.e("day", String.valueOf(c - 47));
                                        calSet.set(Calendar.DAY_OF_WEEK, c - 47);
                                        if (calSet.compareTo(calNow) <= 0) {
                                            calSet.add(Calendar.DATE, 7);
                                        }

                                        String countStr = taskID + String.valueOf(c);
                                        int newCount1 = Integer.parseInt(countStr);
                                        Log.e("newCountOn: ", String.valueOf(newCount1));
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, newCount1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
                                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), (DateUtils.DAY_IN_MILLIS) * 7, pendingIntent);
                                        Log.e("turn on alarm: ",  String.valueOf(newCount1) + "ON");

                                    }

                                } else {
                                    itemref.document(snapshot.getId()).update("isup", 0);
                                    for (char c : days.toCharArray()) {
                                        String countStr = taskID + String.valueOf(c);
                                        int newCount1 = Integer.parseInt(countStr);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, newCount1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        if (alarmManager != null) {
                                            alarmManager.cancel(pendingIntent);
                                        }
                                        Log.e("turn off alarm: ", String.valueOf(newCount1) + "OFF");
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public alarm_add getAlarmAt(int position){
        return list.get(position);
    }

    private String getusermail() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if(user != null) {
            return  user.getEmail();
        }
        return "ANONYMOUS" ;
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
            txttime = itemview.findViewById(R.id.txthistories);
            tg_alarmon = itemview.findViewById(R.id.tg_alarmon);
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getBindingAdapterPosition();
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
