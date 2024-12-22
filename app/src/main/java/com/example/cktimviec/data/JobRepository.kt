package com.example.cktimviec.data

import com.google.firebase.firestore.FirebaseFirestore

class JobRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getJobs(onSuccess: (List<Job>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("jobs")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onFailure(e) // Xử lý lỗi
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val jobList = snapshot.documents.mapNotNull { it.toObject(Job::class.java) }
                    onSuccess(jobList) // Cập nhật danh sách công việc
                }
            }
    }
    fun addJob(job: Job, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val documentRef = firestore.collection("jobs").document() // Tạo ID trước
        val jobWithId = job.copy(id = documentRef.id) // Cập nhật ID vào job
        documentRef.set(jobWithId)
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

    // Thêm phương thức lấy danh sách ứng viên cho công việc
    fun getApplicantsForJob(
        jobId: String,
        onSuccess: (List<Applicant>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("jobs")
            .document(jobId)
            .collection("applicants") // Giả sử ứng viên được lưu trong collection "applicants" dưới mỗi công việc
            .get()
            .addOnSuccessListener { snapshot ->
                val applicantList = snapshot.documents.mapNotNull { it.toObject(Applicant::class.java) }
                onSuccess(applicantList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
