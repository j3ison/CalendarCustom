package com.example.calendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Events> arrayList;
    DBOpenHelper dbOpenHelper;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_events_layout,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Events events = arrayList.get(holder.getAdapterPosition());
        holder.Event.setText(events.getEVENT());
        holder.Datetxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        holder.delete.setOnClickListener(v -> {
            deleteCalendarEvent(events.getEVENT(), events.getDATE(), events.getTIME());
            arrayList.remove(holder.getAdapterPosition());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Datetxt,Event, Time;
        Button delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Datetxt =itemView.findViewById(R.id.eventdate);
            Event = itemView.findViewById(R.id.eventname);
            Time = itemView.findViewById(R.id.eventtime);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    private void deleteCalendarEvent (String event,String date, String time){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database =dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event, date, time, database);
        dbOpenHelper.close();
    }
}
