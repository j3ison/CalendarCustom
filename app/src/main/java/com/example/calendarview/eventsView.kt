package com.example.calendarview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception

class eventsView : ViewModel() {
    val firestoreService= FirestoreService()
    public var listNote: MutableLiveData<List<Events>> = MutableLiveData()
    var isLoading= MutableLiveData<Boolean>()

    fun refresh() {
        getFromFirebase()
    }

    private fun getFromFirebase() {
        firestoreService.getNote(object : Callback<List<Events>> {
            override fun onSuccess(result: List<Events>?) {
                listNote.postValue(result)
                processFinished()
            }
            override fun onFailed(exception: Exception) {
                processFinished()
            }

        })
    }

    fun processFinished(){
        isLoading.value = true
    }
}