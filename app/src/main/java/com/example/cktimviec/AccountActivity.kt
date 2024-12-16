package com.example.cktimviec

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.cktimviec.login.LoginActivity

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Hiển thị thông tin người dùng
        val currentUser = auth.currentUser
        binding.userName.text = currentUser?.displayName ?: "Tên không xác định"
        binding.userEmail.text = currentUser?.email ?: "Email không xác định"

        // Xử lý nút Đăng xuất
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
