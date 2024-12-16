package com.example.cktimviec.nhatuyendung

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.databinding.ActivityEmployerBinding
import com.example.cktimviec.data.Job
import com.example.cktimviec.data.JobRepository
import java.io.ByteArrayOutputStream
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.widget.ArrayAdapter
import android.widget.Spinner

class EmployerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerBinding
    private var selectedImageUri: Uri? = null

    companion object {
        private const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cập nhật dữ liệu cho các Spinner
        val jobTypes = listOf("Toàn thời gian", "Bán thời gian")
        val genderOptions = listOf("Nam", "Nữ", "Không yêu cầu")
        val jobLevels = listOf("Nhân viên", "Trưởng phòng", "Giám đốc")

        val jobTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jobTypes)
        jobTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJobType.adapter = jobTypeAdapter

        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter

        val jobLevelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jobLevels)
        jobLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJobLevel.adapter = jobLevelAdapter

        // Bắt sự kiện chọn ảnh
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        binding.btnPostJob.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val company = binding.etCompany.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val salaryString = binding.etSalary.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val requirements = binding.etRequirements.text.toString().trim()
            val experience = binding.etExperience.text.toString().trim()
            val numberOfPeopleString = binding.etNumberOfPeople.text.toString().trim()
            val deadline = binding.etDeadline.text.toString().trim()

            // Kiểm tra thông tin đã đủ chưa
            if (title.isEmpty() || company.isEmpty() || location.isEmpty() || salaryString.isEmpty() ||
                description.isEmpty() || requirements.isEmpty() || experience.isEmpty() ||
                numberOfPeopleString.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển đổi salary và numberOfPeople từ String thành Long
            val salary = salaryString.toLongOrNull() ?: 0L
            val numberOfPeople = numberOfPeopleString.toIntOrNull() ?: 0

            // Lấy dữ liệu từ Spinner
            val jobType = binding.spinnerJobType.selectedItem.toString()
            val gender = binding.spinnerGender.selectedItem.toString()
            val jobLevel = binding.spinnerJobLevel.selectedItem.toString()

            // Tạo đối tượng Job mới
            val job = Job(
                id = "",
                title = title,
                company = company,
                location = location,
                salary = salary,
                description = description,
                requirements = requirements,
                experience = experience,
                jobType = jobType,
                numberOfPeople = numberOfPeople,
                gender = gender,
                jobLevel = jobLevel,
                deadline = deadline
            )

            // Gửi công việc với ảnh
            postJob(job, selectedImageUri)
        }
    }

    // Xử lý kết quả chọn ảnh từ thư viện
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.ivJobImage.setImageURI(selectedImageUri) // Hiển thị ảnh trong ImageView
        }
    }

    private fun postJob(job: Job, imageUri: Uri?) {
        val jobRepository = JobRepository()
        imageUri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageData = byteArrayOutputStream.toByteArray()

            // Tải ảnh lên Firebase Storage và lấy URL ảnh
            val storageReference = FirebaseStorage.getInstance().reference.child("job_images/${job.id}")
            val uploadTask = storageReference.putBytes(imageData)

            uploadTask.addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val updatedJob = job.copy(id = job.id, imageUrl = imageUrl)
                    jobRepository.addJob(updatedJob, onSuccess = {
                        Toast.makeText(this, "Đăng việc thành công!", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }, onFailure = { e ->
                        Toast.makeText(this, "Đăng việc thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    })
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Tải ảnh lên thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            jobRepository.addJob(job, onSuccess = {
                Toast.makeText(this, "Đăng việc thành công!", Toast.LENGTH_SHORT).show()
                clearFields()
            }, onFailure = { e ->
                Toast.makeText(this, "Đăng việc thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun clearFields() {
        binding.etTitle.text.clear()
        binding.etCompany.text.clear()
        binding.etLocation.text.clear()
        binding.etSalary.text.clear()
        binding.etDescription.text.clear()
        binding.etRequirements.text.clear()
        binding.etExperience.text.clear()
        binding.etNumberOfPeople.text.clear()
        binding.etDeadline.text.clear()
        binding.ivJobImage.setImageResource(android.R.drawable.ic_menu_camera) // Reset ảnh
    }
}
