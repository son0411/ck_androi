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
    val requirements: String = ""
): Parcelable
