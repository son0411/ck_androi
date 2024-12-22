package com.example.cktimviec

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cktimviec.data.Notification
import com.example.cktimviec.databinding.ActivityNotificationBinding
import com.google.firebase.firestore.FirebaseFirestore

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy dữ liệu từ Firestore
        val firestore = FirebaseFirestore.getInstance()

        // Lấy applicantId từ Intent (có thể từ đăng nhập của người dùng)
        val applicantId = "current_user_applicant_id" // Cập nhật ID của người dùng hiện tại tại đây
        if (applicantId.isNullOrEmpty()) {
            Toast.makeText(this, "Ứng viên không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        // Truy vấn thông báo từ Firestore
        firestore.collection("notifications")
            .whereEqualTo("applicantId", applicantId) // Lọc theo ID ứng viên
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Không có thông báo nào", Toast.LENGTH_SHORT).show()
                }

                val notifications = mutableListOf<Notification>()
                for (document in result) {
                    val message = document.getString("message")
                    val timestamp = document.getLong("timestamp")

                    // Kiểm tra nếu thông báo hợp lệ
                    if (message != null && timestamp != null) {
                        // Tạo thông báo từ dữ liệu Firestore
                        notifications.add(Notification(message, timestamp))
                    }
                }

                // Cập nhật adapter với dữ liệu từ Firestore
                notificationAdapter = NotificationAdapter(notifications)
                binding.notificationRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.notificationRecyclerView.adapter = notificationAdapter
            }
            .addOnFailureListener { exception ->
                // Hiển thị lỗi nếu truy vấn thất bại
                Toast.makeText(this, "Lỗi tải thông báo: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
