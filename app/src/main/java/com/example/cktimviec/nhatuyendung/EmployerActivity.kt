package com.example.cktimviec.nhatuyendung

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.cktimviec.databinding.ActivityEmployerBinding
import com.example.cktimviec.data.Job
import com.example.cktimviec.data.JobRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import android.net.Uri
import android.content.Intent

class EmployerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployerBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocationUri: Uri? = null

    companion object {
        private const val IMAGE_REQUEST_CODE = 100
        private const val LOCATION_REQUEST_CODE = 200
        private const val LOCATION_PERMISSION_REQUEST_CODE = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        // Bắt sự kiện chọn địa điểm hiện tại
        binding.btnSelectLocation.setOnClickListener {
            // Kiểm tra quyền truy cập vị trí
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Nếu chưa có quyền, yêu cầu cấp quyền
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
                return@setOnClickListener
            }

            // Lấy vị trí hiện tại
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            binding.etLocation.setText(address.getAddressLine(0)) // Hiển thị địa chỉ
                            Toast.makeText(this, "Địa điểm: ${address.getAddressLine(0)}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Không tìm thấy địa chỉ.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Không thể lấy thông tin địa chỉ: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Không thể lấy vị trí hiện tại.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Bắt sự kiện đăng tuyển
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
                numberOfPeopleString.isEmpty() || deadline.isEmpty()
            ) {
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
            if (selectedLocationUri != null) {
                uploadImageAndPostJob(job, selectedLocationUri!!)
            } else {
                postJob(job, null)
            }
        }

        // Lắng nghe thay đổi trong danh sách công việc
        loadJobs()
    }

    private fun uploadImageAndPostJob(job: Job, imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("job_images/${UUID.randomUUID()}.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val jobWithImage = job.copy(imageUrl = uri.toString())
                    postJob(jobWithImage, null) // Đăng công việc kèm ảnh
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi tải ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun postJob(job: Job, locationUri: Uri?) {
        val jobRepository = JobRepository()

        // Đăng công việc không có ảnh
        jobRepository.addJob(job, onSuccess = {
            Toast.makeText(this, "Đăng việc thành công!", Toast.LENGTH_SHORT).show()
            clearFields()
        }, onFailure = { e ->
            Toast.makeText(this, "Đăng việc thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }

    private fun loadJobs() {
        val jobRepository = JobRepository()

        jobRepository.getJobs(
            onSuccess = { jobs ->
                // Cập nhật giao diện hoặc danh sách công việc
                updateJobList(jobs)
            },
            onFailure = { e ->
                Toast.makeText(this, "Lỗi tải công việc: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateJobList(jobs: List<Job>) {
        // Cập nhật dữ liệu lên RecyclerView hoặc ListView
        // jobAdapter.submitList(jobs) // Giả sử bạn có adapter tên là jobAdapter
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

    // Xử lý kết quả chọn ảnh từ thư viện và vị trí hiện tại
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedLocationUri = data.data
            binding.ivJobImage.setImageURI(selectedLocationUri) // Hiển thị ảnh trong ImageView
        }
    }

    // Xử lý kết quả yêu cầu quyền truy cập vị trí
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu người dùng cấp quyền, gọi lại hàm lấy vị trí
                binding.btnSelectLocation.performClick()
            } else {
                Toast.makeText(this, "Cần quyền truy cập vị trí để lấy địa điểm", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
