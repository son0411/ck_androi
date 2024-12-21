package com.example.cktimviec.nhatuyendung

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.data.Job
import com.example.cktimviec.databinding.ActivityJobDetailNtdBinding
import android.content.Intent
import com.example.cktimviec.data.JobRepository

class JobDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailNtdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailNtdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy thông tin công việc từ Intent
        val job = intent.getParcelableExtra<Job>("job")

        if (job != null) {
            // Hiển thị thông tin chi tiết công việc
            binding.tvTitle.text = job.title
            binding.tvCompany.text = job.company
            binding.tvLocation.text = job.location
            binding.tvSalary.text = "Mức lương: ${job.salary} USD"
            binding.tvDescription.text = job.description
            binding.tvRequirements.text = "Yêu cầu: ${job.requirements}"
            binding.tvExperience.text = "Kinh nghiệm: ${job.experience}"
            binding.tvJobType.text = "Hình thức: ${job.jobType}"
            binding.tvNumberOfPeople.text = "Số người cần tuyển: ${job.numberOfPeople}"
            binding.tvGender.text = "Giới tính: ${job.gender}"
            binding.tvJobLevel.text = "Cấp bậc: ${job.jobLevel}"
            binding.tvDeadline.text = "Hạn nộp hồ sơ: ${job.deadline}"

            // Cấu hình nút Sửa
            binding.btnEdit.setOnClickListener {
                // Mở màn hình sửa công việc
                val editIntent = Intent(this, EmployerActivity::class.java)
                editIntent.putExtra("job", job)  // Truyền công việc cần sửa
                startActivity(editIntent)
            }

            // Cấu hình nút Xóa
            binding.btnDelete.setOnClickListener {
                // Xử lý xóa công việc
                deleteJob(job)
            }
        }
    }

    // Hàm xóa công việc
    private fun deleteJob(job: Job) {
        val jobRepository = JobRepository()
        jobRepository.deleteJob(job.id, onSuccess = {
            Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show()
            finish()  // Quay lại màn hình trước
        }, onFailure = {
            Toast.makeText(this, "Không thể xóa công việc: ${it.message}", Toast.LENGTH_SHORT).show()
        })
    }
}
