package com.example.calendarview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CustomCalendarView extends LinearLayout {
    ImageButton NextButton, PreviousButton;
    TextView CurrentDate;
    GridView gridView;

    private static final int MAX_CALENDAR_DAYS=42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;

    SimpleDateFormat dateFormat = new SimpleDateFormat ("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat ("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat ("yyyy", Locale.ENGLISH);
    SimpleDateFormat eventdateFormat = new SimpleDateFormat ("yyyy-MM-dd", Locale.ENGLISH);

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();

    DBOpenHelper dbOpenHelper;
    DBfirebase dBfirebase=new DBfirebase();
    public CustomCalendarView(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @SuppressLint("NotifyDataSetChanged")
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        InitializeLayout();
        SetUpCalendar();

        PreviousButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            SetUpCalendar();
        });

        NextButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            SetUpCalendar();
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout, null);
            EditText EventName = addView.findViewById(R.id.event_name);
            TextView EventTime =addView.findViewById(R.id.eventtime);
            ImageButton SetTime = addView.findViewById(R.id.seteventtime);
            Button AddEvent = addView.findViewById(R.id.addevent);
            SetTime.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int hours =calendar.get(Calendar.HOUR_OF_DAY);
                int minuts = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog
                                , (view1, hourOfDay, minute) -> {
                                    Calendar c= Calendar.getInstance();
                                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    c.set(Calendar.MINUTE, minute);
                                    c.setTimeZone(TimeZone.getDefault());
                                    SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                    String event_Time = hformate.format(c.getTime());
                                    EventTime.setText(event_Time);
                                },hours, minuts, false);
                timePickerDialog.show();
            });

            final String date =eventdateFormat.format(dates.get(position));
            final String month =monthFormat.format(dates.get(position));
            final String year =yearFormat.format(dates.get(position));

            AddEvent.setOnClickListener(v -> {
                SaveEvent(EventName.getText().toString(), EventTime.getText().toString(),date,month,year);
                SetUpCalendar();
                alertDialog.dismiss();
            });

            builder.setView(addView);
            alertDialog = builder.create();
            alertDialog.show();
        });

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            String date= eventdateFormat.format(dates.get(position));
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(true);
            View showView= LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout,null);
            RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
            RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(showView.getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext()
                    ,CollectEventByDate(date));
            recyclerView.setAdapter(eventRecyclerAdapter);
            eventRecyclerAdapter.notifyDataSetChanged();

            builder.setView(showView);
            alertDialog = builder.create();
            alertDialog.show();

            alertDialog.setOnCancelListener(dialog -> SetUpCalendar());
            return true;
        });
    }

    private ArrayList<Events> CollectEventByDate(String date) {
        ArrayList<Events> arrayList = new ArrayList<>();
        eventsList.clear();
        eventsList.addAll(dBfirebase.getEvents());
        arrayList.clear();
        arrayList.addAll(eventsList);

        /*dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEvents(date, database);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            @SuppressLint("Range") String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            @SuppressLint("Range") String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            @SuppressLint("Range") String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            Events events = new Events(event, time, Date, month, Year);
            eventsList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();*/
        return arrayList;
    }

    private void SaveEvent (String event, String time, String date, String month, String year){
        dBfirebase.saveEvents(event,time,date,month,year);
        /*dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.SaveEvent(event, time, date, month, year, database);
        dbOpenHelper.close();*/
        Toast.makeText(context, "Cita Guardada", Toast.LENGTH_SHORT).show();
    }

    private void InitializeLayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton = view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridview);
        CollectEventByDate("");
    }

    private void SetUpCalendar(){
        String currwntDate= dateFormat.format(calendar.getTime());
        CurrentDate.setText(currwntDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) -1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);
        CollectEventsforMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));
        CollectEventByDate(monthFormat.format(calendar.getTime()));
        while (dates.size() < MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        myGridAdapter = new MyGridAdapter(context,dates,calendar,eventsList);
        gridView.setAdapter(myGridAdapter);
    }

    private void CollectEventsforMonth (String Month, String year){
        eventsList.clear();
        eventsList.addAll(dBfirebase.getEvents());
        /*dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database= dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadEventsforMonth(Month,year,database);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String event =cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            @SuppressLint("Range") String time =cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            @SuppressLint("Range") String date =cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            @SuppressLint("Range") String month =cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            @SuppressLint("Range") String Year =cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            Events events= new Events(event,time, date, month, Year);
            eventsList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();*/
    }
}
