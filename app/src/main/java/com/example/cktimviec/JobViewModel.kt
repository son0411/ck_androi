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
                    val imageUrl = doc.getString("imageUrl") ?: ""
                    val experience = doc.getString("experience") ?: ""
                    val jobType = doc.getString("jobType") ?: ""
                    val numberOfPeople = doc.getLong("numberOfPeople")?.toInt() ?: 0
                    val gender = doc.getString("gender") ?: ""
                    val jobLevel = doc.getString("jobLevel") ?: ""
                    val deadline = doc.getString("deadline") ?: ""

                    Job(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        company = doc.getString("company") ?: "",
                        location = doc.getString("location") ?: "",
                        salary = salary,
                        description = doc.getString("description") ?: "",
                        requirements = doc.getString("requirements") ?: "",
                        imageUrl = imageUrl,
                        experience = experience,
                        jobType = jobType,
                        numberOfPeople = numberOfPeople,
                        gender = gender,
                        jobLevel = jobLevel,
                        deadline = deadline
                    )
                }
                _jobs.value = jobList
            }
            .addOnFailureListener {
                _jobs.value = emptyList()
            }
    }
}
