package com.example.cktimviec

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cktimviec.data.Job
import com.example.cktimviec.databinding.ActivityMainBinding
import com.example.cktimviec.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val jobViewModel: JobViewModel by viewModels()
    private lateinit var jobAdapter: JobAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var handler: Handler
    private var bannerIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        jobAdapter = JobAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = jobAdapter

        jobViewModel.jobs.observe(this) { jobs ->
            jobAdapter.updateList(jobs)
        }

        // Thay đổi banner mỗi 5 giây
        handler = Handler()
        startBannerRotation()
    }

    private fun startBannerRotation() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                changeBanner()
                handler.postDelayed(this, 2000)  // 5 giây sau sẽ chạy lại
            }
        }, 2000)  // Bắt đầu sau 5 giây
    }

    private fun changeBanner() {
        // Ẩn tất cả banner trước
        binding.banner1.visibility = android.view.View.GONE
        binding.bnhai.visibility = android.view.View.GONE
        binding.banner3.visibility = android.view.View.GONE

        // Hiển thị banner theo index
        when (bannerIndex) {
            0 -> binding.banner1.visibility = android.view.View.VISIBLE
            1 -> binding.bnhai.visibility = android.view.View.VISIBLE
            2 -> binding.banner3.visibility = android.view.View.VISIBLE
        }

        // Cập nhật index cho lần thay đổi tiếp theo
        bannerIndex = (bannerIndex + 1) % 3
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)  // Xoá handler khi activity bị hủy
    }
}
