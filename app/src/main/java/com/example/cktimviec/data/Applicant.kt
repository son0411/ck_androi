package com.example.cktimviec.data

data class Applicant(
    val id: String,          // ID của tài liệu Firestore
    val name: String,        // Tên ứng viên
    val email: String,       // Email ứng viên
    val phone: String,       // Số điện thoại ứng viên
    val position: String,    // Vị trí công việc
    val contact: String,     // Thông tin liên hệ
    val cvUrl: String,       // Đường dẫn tới CV
    val notes: String,       // Ghi chú thêm
    val timestamp: Long,     // Thời gian đăng tin
    val jobTitle: String,    // Chức danh công việc
    val jobCompany: String,  // Tên công ty
    val jobLocation: String, // Vị trí công việc
    val jobSalary: Long,     // Mức lương công việc
    val jobDescription: String, // Mô tả công việc
    val userId: String       // Thêm trường userId
)
