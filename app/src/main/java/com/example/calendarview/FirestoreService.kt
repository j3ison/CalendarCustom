package com.example.calendarview

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class FirestoreService {
    var COLLECTION = "Eventos"
    val firebaseFirestore = FirebaseFirestore.getInstance()
    val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    init{
        firebaseFirestore.firestoreSettings=settings
    }



    fun getNote(callback: Callback<List<Events>>) {
        firebaseFirestore.collection(COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for(doc in result) {
                    val list=result.toObjects(Events::class.java)
                    callback.onSuccess(list)
                    break
                }
            }
    }
}