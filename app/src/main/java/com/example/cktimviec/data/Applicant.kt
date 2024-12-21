package com.example.cktimviec.data

data class Applicant(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val position: String = "",
    val contact: String = "",
    val cvUrl: String? = null, // Đường dẫn tới file CV (nếu có)
    val notes: String = ""     // Ghi chú của nhà tuyển dụng
)
