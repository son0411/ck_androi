package com.example.cktimviec

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cktimviec.data.Job
import com.example.cktimviec.databinding.ActivityJobDetailBinding

class JobDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận dữ liệu Job từ Intent
        val job = intent.getParcelableExtra<Job>("job_data")
        job?.let {
            displayJobDetails(it)
        }

        // Sự kiện nút Liên hệ nhà tuyển dụng
        binding.btnContactEmployer.setOnClickListener {
            // Ví dụ: mở ứng dụng gửi email hoặc gọi điện
        }

        // Sự kiện nút Ứng tuyển
        binding.btnApplyJob.setOnClickListener {
            // Chuyển sang ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("job_id", job?.id) // Gửi thêm thông tin job_id
            startActivity(intent)
        }
    }

    private fun displayJobDetails(job: Job) {
        binding.tvJobTitle.text = job.title
        binding.tvCompanyName.text = job.company
        binding.tvJobLocation.text = "Địa điểm: ${job.location}"
        binding.tvJobSalary.text = "Lương: ${job.salary} VND"
        binding.tvJobDescription.text = job.description
        binding.tvJobRequirements.text = job.requirements
        binding.tvJobExperience.text = "Kinh nghiệm: ${job.experience}"
        binding.tvJobType.text = "Hình thức: ${job.jobType}"
        binding.tvJobNumberOfPeople.text = "Số lượng người cần tuyển: ${job.numberOfPeople}"
        binding.tvJobGender.text = "Giới tính: ${job.gender}"
        binding.tvJobLevel.text = "Cấp bậc: ${job.jobLevel}"
        binding.tvJobDeadline.text = "Hạn nộp hồ sơ: ${job.deadline}"

        Glide.with(this)
            .load(job.imageUrl)
            .placeholder(android.R.drawable.ic_menu_camera)
            .error(android.R.drawable.ic_dialog_alert)
            .into(binding.ivJobImage)
    }
}
