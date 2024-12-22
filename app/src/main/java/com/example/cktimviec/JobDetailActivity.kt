package com.example.cktimviec

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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

        // Xử lý sự kiện khi nhấn vào vị trí
        binding.tvJobLocation.setOnClickListener {
            job?.location?.let { location ->
                try {
                    // Tạo URI cho vị trí
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(location)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                    // Đặt ứng dụng Google Maps để mở
                    mapIntent.setPackage("com.google.android.apps.maps")

                    // Kiểm tra xem có ứng dụng hỗ trợ hay không
                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        // Nếu không tìm thấy Google Maps, mở trình duyệt mặc định
                        val browserIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        startActivity(browserIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Hiển thị thông báo lỗi nếu xảy ra sự cố
                    Toast.makeText(this, "Không thể mở bản đồ", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Sự kiện nút Liên hệ nhà tuyển dụng
        binding.btnContactEmployer.setOnClickListener {
            // Ví dụ: mở ứng dụng gửi email hoặc gọi điện
        }

        // Xử lý sự kiện nút Back
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Xử lý sự kiện nút Share
        binding.btnShare.setOnClickListener {
            val shareText = """
            Công việc: ${job?.title}
            Công ty: ${job?.company}
            Mức lương: ${job?.salary} USD
            Địa điểm: ${job?.location}
            Kinh nghiệm: ${job?.experience}
            Hãy ứng tuyển ngay tại: [Link công việc]
        """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_STREAM, Uri.parse(job?.imageUrl))
            }
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ công việc qua"))
        }

        // Sự kiện nút Ứng tuyển
        binding.btnApplyJob.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("job_id", job?.id)
            startActivity(intent)
        }
    }

    private fun displayJobDetails(job: Job) {
        binding.tvJobTitle.text = job.title
        binding.tvCompanyName.text = job.company
        binding.tvJobLocation.text = job.location
        binding.tvJobSalary.text = "Mức lương: ${job.salary} USD"
        binding.tvJobDescription.text = "Mô tả công việc: ${job.description}"
        binding.tvJobRequirements.text = "Yêu cầu: ${job.requirements}"
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
