package com.example.cktimviec.data

import com.google.firebase.firestore.FirebaseFirestore

class ApplicantRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getApplicants(onSuccess: (List<Applicant>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("cv_posts")  // Lấy dữ liệu từ collection cv_posts
            .get()
            .addOnSuccessListener { result ->
                val applicants = mutableListOf<Applicant>()
                for (document in result) {
                    // Lấy các trường dữ liệu từ Firestore
                    val cvUrl = document.getString("cvUrl") ?: ""
                    val timestamp = document.getLong("timestamp") ?: 0L
                    val jobTitle = document.getString("jobTitle") ?: ""
                    val jobCompany = document.getString("jobCompany") ?: ""
                    val jobLocation = document.getString("jobLocation") ?: ""
                    val jobSalary = document.getLong("jobSalary") ?: 0L
                    val jobDescription = document.getString("jobDescription") ?: ""
                    val userId = document.getString("userId") ?: ""  // Lấy userId từ Firestore

                    // Tạo đối tượng Applicant từ dữ liệu Firestore
                    val applicant = Applicant(
                        id = document.id,  // ID của tài liệu Firestore
                        name = document.getString("name") ?: "",
                        email = document.getString("email") ?: "",
                        phone = document.getString("phone") ?: "",
                        position = document.getString("position") ?: "",
                        contact = document.getString("contact") ?: "",
                        cvUrl = cvUrl,
                        notes = document.getString("notes") ?: "",
                        timestamp = timestamp,
                        jobTitle = jobTitle,
                        jobCompany = jobCompany,
                        jobLocation = jobLocation,
                        jobSalary = jobSalary,
                        jobDescription = jobDescription,
                        userId = userId  // Thêm userId vào đối tượng Applicant
                    )
                    applicants.add(applicant)
                }
                onSuccess(applicants)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
