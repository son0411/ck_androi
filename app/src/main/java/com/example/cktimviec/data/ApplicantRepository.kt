package com.example.cktimviec.data

import com.google.firebase.firestore.FirebaseFirestore

class ApplicantRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Hàm lấy danh sách ứng viên
    fun getApplicants(
        onSuccess: (List<Applicant>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("applicants")
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
