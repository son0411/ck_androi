package com.example.cktimviec

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class ProfileActivity : AppCompatActivity() {
    private lateinit var btnUpload: Button
    private lateinit var tvSelectedFile: TextView
    private val PICK_FILE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Log.d("ProfileActivity", "ProfileActivity started")

        btnUpload = findViewById(R.id.btn_upload)
        tvSelectedFile = findViewById(R.id.tv_selected_file)

        // Xử lý khi nhấn nút Chọn tệp
        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST)
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
}
