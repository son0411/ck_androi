package com.example.cktimviec

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cktimviec.login.LoginActivity
class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Hiển thị thông tin người dùng
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Lấy thông tin người dùng từ Firestore
            val userId = currentUser.uid
            val userRef = firestore.collection("users").document(userId)

            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("name") ?: "Nguyễn Quốc Sơn"
                    val userCode = document.getString("userCode") ?: "411"
                    val experience = document.getString("experience") ?: "Chưa cập nhật"
                    val desiredJob = document.getString("desiredJob") ?: "Chưa cập nhật"
                    val workLocation = document.getString("jobLocation") ?: "Chưa cập nhật"

                    // Cập nhật UI
                    binding.userName.text = userName
                    binding.userCode.text = "Mã ứng viên: $userCode"
                    binding.experienceText.text = experience // Hiển thị kinh nghiệm làm việc
                    binding.desiredJobText.text = desiredJob // Hiển thị công việc mong muốn
                    binding.workLocationText.text = workLocation // Hiển thị địa chỉ làm việc
                } else {
                    Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Lỗi khi lấy dữ liệu: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Nếu người dùng chưa đăng nhập
            Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show()
        }


        // Switch trạng thái tìm việc
        binding.jobStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Trạng thái tìm việc: Bật" else "Trạng thái tìm việc: Tắt"
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }

        // Switch cho phép NTD liên hệ
        binding.contactPermissionSwitch.setOnCheckedChangeListener { _, isChecked ->
            val permission = if (isChecked) "NTD có thể liên hệ" else "NTD không thể liên hệ"
            Toast.makeText(this, permission, Toast.LENGTH_SHORT).show()
        }

        // Xử lý đăng xuất
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
