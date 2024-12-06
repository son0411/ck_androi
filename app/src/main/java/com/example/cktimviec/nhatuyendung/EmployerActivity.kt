package com.example.cktimviec.nhatuyendung

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.databinding.ActivityEmployerBinding
import com.example.cktimviec.data.Job
import com.example.cktimviec.data.JobRepository

class EmployerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPostJob.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val company = binding.etCompany.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val salaryString = binding.etSalary.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val requirements = binding.etRequirements.text.toString().trim()

            if (title.isEmpty() || company.isEmpty() || location.isEmpty() || salaryString.isEmpty() ||
                description.isEmpty() || requirements.isEmpty()
            ) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển đổi salary từ String thành Long
            val salary = salaryString.toLongOrNull()
                ?: 0L // Nếu không thể chuyển đổi, gán giá trị mặc định là 0L

            val job = Job(
                id = "", // ID sẽ được tự động tạo bởi Firebase
                title = title,
                company = company,
                location = location,
                salary = salary, // Gán giá trị Long cho salary
                description = description,
                requirements = requirements
            )
            postJob(job)
        }
    }

        private fun postJob(job: Job) {
        val jobRepository = JobRepository()
        jobRepository.addJob(job,
            onSuccess = {
                // Xử lý thành công
                Toast.makeText(this, "Đăng việc thành công!", Toast.LENGTH_SHORT).show()
                clearFields() // Xóa các trường sau khi đăng thành công
            },
            onFailure = { e ->
                // Xử lý lỗi
                Toast.makeText(this, "Đăng việc thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun clearFields() {
        binding.etTitle.text.clear()
        binding.etCompany.text.clear()
        binding.etLocation.text.clear()
        binding.etSalary.text.clear()
        binding.etDescription.text.clear()
        binding.etRequirements.text.clear()
    }
}
