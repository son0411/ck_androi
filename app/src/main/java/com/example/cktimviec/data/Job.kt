package com.example.cktimviec.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Job(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val salary: Long = 0L,  // Đổi từ String sang Long
    val description: String = "",
    val requirements: String = "",
    val experience: String = "",  // Kinh nghiệm
    val jobType: String = "",  // Hình thức (toàn thời gian, bán thời gian)
    val numberOfPeople: Int = 0,  // Số lượng người cần tuyển
    val gender: String = "",  // Giới tính
    val jobLevel: String = "",  // Cấp bậc
    val deadline: String = "",  // Hạn nộp hồ sơ
    val imageUrl: String = ""  // URL ảnh
): Parcelable
