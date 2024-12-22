package com.example.cktimviec.nhatuyendung

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.R
import com.google.firebase.firestore.FirebaseFirestore

class ContactAdminActivity : AppCompatActivity() {

    private lateinit var etNotificationMessage: EditText
    private lateinit var btnSendNotification: Button
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_admin)

        etNotificationMessage = findViewById(R.id.etNotificationMessage)
        btnSendNotification = findViewById(R.id.btnSendNotification)

        // Lấy userId của ứng viên từ Intent (chuyển từ màn hình trước đó)
        val applicantId = intent.getStringExtra("APPLICANT_ID")

        // Xử lý sự kiện khi admin gửi thông báo
        btnSendNotification.setOnClickListener {
            val notificationMessage = etNotificationMessage.text.toString()
            if (notificationMessage.isNotEmpty() && applicantId != null) {
                sendNotificationToApplicant(applicantId, notificationMessage)
            } else {
                Toast.makeText(this, "Vui lòng nhập thông báo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendNotificationToApplicant(applicantId: String, message: String) {
        val notification = mapOf(
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "applicantId" to applicantId
        )

        firestore.collection("notifications")
            .add(notification)
            .addOnSuccessListener {
                Toast.makeText(this, "Thông báo đã được gửi", Toast.LENGTH_SHORT).show()
                finish() // Đóng màn hình nhập thông báo sau khi gửi thành công
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gửi thông báo thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
