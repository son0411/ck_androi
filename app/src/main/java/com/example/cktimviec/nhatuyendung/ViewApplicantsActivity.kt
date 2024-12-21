package com.example.cktimviec.nhatuyendung

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cktimviec.databinding.ActivityViewApplicantsBinding
import com.example.cktimviec.data.Applicant
import com.example.cktimviec.data.ApplicantRepository

class ViewApplicantsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewApplicantsBinding
    private val applicantRepository = ApplicantRepository()
    private val applicantsList = mutableListOf<Applicant>()
    private lateinit var applicantAdapter: ApplicantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cấu hình RecyclerView
        applicantAdapter = ApplicantAdapter(applicantsList)
        binding.recyclerViewApplicants.apply {
            layoutManager = LinearLayoutManager(this@ViewApplicantsActivity)
            adapter = applicantAdapter
        }

        // Tải danh sách ứng viên
        loadApplicants()
    }

    private fun loadApplicants() {
        applicantRepository.getApplicants(
            onSuccess = { applicants ->
                applicantsList.clear()
                applicantsList.addAll(applicants)
                applicantAdapter.notifyDataSetChanged()
            },
            onFailure = { e ->
                Toast.makeText(this, "Không tải được danh sách ứng viên: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
