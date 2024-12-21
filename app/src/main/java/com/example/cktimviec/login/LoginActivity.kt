package com.example.cktimviec.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.MainActivity
import com.example.cktimviec.databinding.ActivityLoginBinding
import com.example.cktimviec.nhatuyendung.EmployerMainActivity
import com.example.cktimviec.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Kiểm tra nếu email và password không trống
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút chuyển sang màn hình đăng ký
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUserRole()
                } else {
                    // Xử lý lỗi nếu đăng nhập thất bại
                    Toast.makeText(this, "Đăng nhập thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserRole() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role")
                        if (role == "admin") {
                            startActivity(Intent(this, EmployerMainActivity::class.java))
                        } else {
                            // Kiểm tra xem người dùng đã cung cấp thông tin chưa
                            firestore.collection("userInfo").document(userId)
                                .get()
                                .addOnSuccessListener { userInfoDoc ->
                                    if (userInfoDoc.exists()) {
                                        startActivity(Intent(this, MainActivity::class.java))
                                    } else {
                                        startActivity(Intent(this, ChatActivity::class.java))
                                    }
                                    finish()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Người dùng không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
        }
    }
}
