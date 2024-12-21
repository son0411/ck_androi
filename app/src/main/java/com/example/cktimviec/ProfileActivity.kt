package com.example.cktimviec

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var btnUpload: Button
    private lateinit var btnPost: Button
    private lateinit var tvSelectedFile: TextView

    private val PICK_FILE_REQUEST = 1
    private val REQUEST_CODE = 100
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btnUpload = findViewById(R.id.btn_upload)
        btnPost = findViewById(R.id.btn_post)
        tvSelectedFile = findViewById(R.id.tv_selected_file)

        // Khởi tạo Firebase
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        // Kiểm tra quyền
        checkStoragePermission()

        // Chọn tệp
        btnUpload.setOnClickListener {
            if (hasStoragePermission()) {
                openFilePicker()
            } else {
                requestStoragePermission()
            }
        }

        // Đăng tệp lên Firebase
        btnPost.setOnClickListener {
            if (selectedFileUri != null) {
                uploadFileToFirebase(selectedFileUri!!)
            } else {
                showToast("Vui lòng chọn CV trước khi đăng.")
            }
        }
    }

    // Kiểm tra quyền truy cập bộ nhớ
    private fun checkStoragePermission() {
        if (!hasStoragePermission()) {
            requestStoragePermission()
        }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    // Xử lý kết quả chọn tệp
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri
                val fileName = getFileName(uri)
                tvSelectedFile.text = "Tệp đã chọn: $fileName"
            } ?: showToast("Không thể chọn tệp.")
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName = "unknown"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) {
                fileName = cursor.getString(index)
            }
        }
        return fileName
    }

    private fun uploadFileToFirebase(fileUri: Uri) {
        val fileName = "cv_${UUID.randomUUID()}.pdf"
        val fileRef = storageReference.child("cv_files/$fileName")

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
            if (inputStream != null) {
                fileRef.putStream(inputStream)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                            saveFileUrlToFirestore(uri.toString())
                        }
                    }
                    .addOnFailureListener { e ->
                        showToast("Lỗi khi tải tệp: ${e.message}")
                    }
            } else {
                showToast("Không thể đọc tệp đã chọn.")
            }
        } catch (e: Exception) {
            showToast("Lỗi: ${e.message}")
        }
    }

    private fun saveFileUrlToFirestore(fileUrl: String) {
        val cvData = hashMapOf(
            "cvUrl" to fileUrl,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("cv_posts")
            .add(cvData)
            .addOnSuccessListener {
                showToast("Đã đăng CV thành công!")
                finish()
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi lưu dữ liệu: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Xử lý quyền
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showToast("Quyền truy cập bộ nhớ đã được cấp!")
        } else {
            showToast("Quyền truy cập bị từ chối.")
        }
    }
}