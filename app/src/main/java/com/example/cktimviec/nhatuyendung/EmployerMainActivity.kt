package com.example.cktimviec.nhatuyendung

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cktimviec.databinding.ActivityEmployerMainBinding
import com.example.cktimviec.data.Job
import com.example.cktimviec.data.JobRepository

class EmployerMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployerMainBinding
    private val jobRepository = JobRepository()
    private val jobList = mutableListOf<Job>()
    private lateinit var jobAdapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cấu hình RecyclerView
        jobAdapter = JobAdapter(jobList, onItemClick = { job ->
            // Truyền công việc vào JobDetailActivity
            val intent = Intent(this, JobDetailActivity::class.java)
            intent.putExtra("job", job)  // Truyền dữ liệu công việc
            startActivity(intent)
        })

        binding.recyclerViewJobs.apply {
            layoutManager = LinearLayoutManager(this@EmployerMainActivity)
            adapter = jobAdapter
        }

        // Tải danh sách công việc
        loadJobs()

        // Nút thêm công việc
        binding.fabAddJob.setOnClickListener {
            val intent = Intent(this, EmployerActivity::class.java)
            startActivity(intent)
        }

        // Nút xem ứng viên
        binding.fabViewApplicants.setOnClickListener {
            // Giả sử bạn muốn xem danh sách ứng viên cho tất cả các công việc
            val intent = Intent(this, ViewApplicantsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadJobs() {
        jobRepository.getJobs(
            onSuccess = { jobs ->
                jobList.clear()
                jobList.addAll(jobs)
                jobAdapter.notifyDataSetChanged()
            },
            onFailure = { e ->
                Toast.makeText(this, "Không tải được danh sách công việc: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
