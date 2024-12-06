package com.example.cktimviec

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cktimviec.data.Job
import com.google.firebase.firestore.FirebaseFirestore

class JobViewModel : ViewModel() {
    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> get() = _jobs

    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchJobsFromFirestore()
    }

    private fun fetchJobsFromFirestore() {
        firestore.collection("jobs")
            .get()
            .addOnSuccessListener { documents ->
                val jobList = documents.map { doc ->
                    // Fetch salary as Long
                    val salary = doc.getLong("salary") ?: 0L // Default to 0 if not found

                    Job(
                        title = doc.getString("title") ?: "",
                        company = doc.getString("company") ?: "",
                        location = doc.getString("location") ?: "",
                        salary = salary
                    )
                }
                _jobs.value = jobList
            }
            .addOnFailureListener {
                _jobs.value = emptyList()
            }
    }

}
