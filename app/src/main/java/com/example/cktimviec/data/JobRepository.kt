package com.example.cktimviec.data

import com.google.firebase.firestore.FirebaseFirestore

class JobRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getJobs(onSuccess: (List<Job>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("jobs")
            .get()
            .addOnSuccessListener { snapshot ->
                val jobList = snapshot.documents.mapNotNull { it.toObject(Job::class.java) }
                onSuccess(jobList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addJob(job: Job, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("jobs").add(job)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun updateJob(job: Job, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("jobs").document(job.id)
            .set(job)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun deleteJob(jobId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("jobs").document(jobId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}
