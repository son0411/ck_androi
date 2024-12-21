package com.example.cktimviec.nhatuyendung

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cktimviec.databinding.ActivityApplicantListBinding
import com.example.cktimviec.data.Applicant
import com.example.cktimviec.data.JobRepository

class ApplicantListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplicantListBinding
    private val applicantList = mutableListOf<Applicant>()
    private lateinit var applicantAdapter: ApplicantAdapter
    private lateinit var jobId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicantListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy ID công việc từ Intent
        jobId = intent.getStringExtra("jobId") ?: return

        // Cấu hình RecyclerView
        applicantAdapter = ApplicantAdapter(applicantList)
        binding.recyclerViewApplicants.apply {
            layoutManager = LinearLayoutManager(this@ApplicantListActivity)
            adapter = applicantAdapter
        }

        // Tải danh sách ứng viên
        loadApplicants()
    }

    private fun loadApplicants() {
        val jobRepository = JobRepository()

        // Gọi phương thức getApplicantsForJob từ JobRepository
        jobRepository.getApplicantsForJob(
            jobId = jobId,
            onSuccess = { applicants ->
                applicantList.clear()
                applicantList.addAll(applicants)
                applicantAdapter.notifyDataSetChanged()
            },
            onFailure = { e ->
                Toast.makeText(this, "Không tải được danh sách ứng viên: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
