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
                                if(isChecked){
                                    itemref.document(snapshot.getId()).update("isup", 1);
                                } else {
                                    itemref.document(snapshot.getId()).update("isup", 0);
                                }
                            }
                        }
                    }
                });
            }
        });


        /*

        holder.tg_alarmon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alarmID = list.get(safeposition).getAlarmID();
                Log.e("UPDATE", alarmID);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                CollectionReference itemref = database.collection(getusermail()).document("Alarm")
                        .collection("alarms");
                Query update = itemref.whereEqualTo("alarmID", alarmID);
                update.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot snapshot : task.getResult()){
                                alarm_add alarms = snapshot.toObject(alarm_add.class);
                                if (alarms.getIsup() == 1) {
                                    itemref.document(snapshot.getId()).update("isup", 0);
                                   // holder.tg_alarmon.setChecked(false);
                                } else {
                                    itemref.document(snapshot.getId()).update("isup", 1);
                                    //holder.tg_alarmon.setChecked(true);
                                }
                            }
                        } else {
                            Log.e("UPDATE", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        }); //toggle end


         */

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
            txttime = itemview.findViewById(R.id.txtTime);
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
