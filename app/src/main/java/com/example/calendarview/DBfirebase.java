package com.example.calendarview;

import static android.content.ContentValues.TAG;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class DBfirebase  {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings= new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();

    String COLLECTION = "Eventos";

    List<Events> listEvents= new ArrayList<>();
    eventsView viewModel;

    public DBfirebase() {
        firebaseFirestore.setFirestoreSettings(settings);
        /*viewModel= new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(eventsView.class);
        viewModel.refresh();*/
    }

    public void saveEvents(String event, String time,String date, String month,String year){
        Events events = new Events(event,time,date,month,year);
        firebaseFirestore.collection(COLLECTION).document().set(events);
    }

    public List<Events> getEvents(){

        /*viewModel.getListNote().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<List<Events>>() {
            @Override
            public void onChanged(List<Events> events) {
                listEvents.addAll(events);
            }
        });*/
        firebaseFirestore.collection(COLLECTION).addSnapshotListener(
                new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if(value !=null){
                            listEvents.clear();
                            for(QueryDocumentSnapshot doc : value){
                                Events events = doc.toObject(Events.class);
                                listEvents.add(events);
                            }
                        }
                    }
                }
        );

        /*firebaseFirestore.collection(COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Events events = document.toObject(Events.class);
                                listEvents.add(events);
                            }
                        }
                    }
                });*/
        return listEvents;
    }

    public void deleteEvents(String event, String date, String time){

    }


}
