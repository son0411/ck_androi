package com.example.cktimviec

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var btnUpload: Button
    private lateinit var btnPost: Button  // Khai báo nút Đăng
    private lateinit var tvSelectedFile: TextView
    private val PICK_FILE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btnUpload = findViewById(R.id.btn_upload)
        btnPost = findViewById(R.id.btn_post)  // Ánh xạ nút Đăng
        tvSelectedFile = findViewById(R.id.tv_selected_file)

        // Xử lý khi nhấn nút Tải CV
        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }

        // Xử lý khi nhấn nút Đăng
        btnPost.setOnClickListener {
            // Kiểm tra nếu đã chọn tệp
            val selectedFile = tvSelectedFile.text.toString()
            if (selectedFile.contains("Tệp đã chọn:")) {
                // Hiển thị thông báo đăng thành công
                showToast("Đã đăng CV thành công")
            } else {
                // Nếu chưa chọn tệp, yêu cầu người dùng chọn tệp
                showToast("Vui lòng chọn CV trước khi đăng")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            fileUri?.let {
                tvSelectedFile.text = "Tệp đã chọn: ${it.path}"
            } ?: run {
                tvSelectedFile.text = "Không chọn được tệp"
            }
        }
    }

    // Hàm hiển thị thông báo Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
