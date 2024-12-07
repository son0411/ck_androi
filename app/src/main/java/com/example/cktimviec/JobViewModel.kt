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
                    val salary = doc.getLong("salary") ?: 0L
                    val imageUrl = doc.getString("imageUrl") ?: "" // Lấy đường dẫn ảnh, có thể trống

                    Job(
                        title = doc.getString("title") ?: "",
                        company = doc.getString("company") ?: "",
                        location = doc.getString("location") ?: "",
                        salary = salary,
                        imageUrl = imageUrl // Gán đường dẫn ảnh vào Job
                    )
                }
                _jobs.value = jobList
            }
            .addOnFailureListener {
                _jobs.value = emptyList()
            }
    }
}
