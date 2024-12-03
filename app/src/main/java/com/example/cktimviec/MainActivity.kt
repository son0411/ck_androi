package com.example.cktimviec

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.EditorInfo
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

        // Kiểm tra người dùng đã đăng nhập
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Cấu hình RecyclerView
        jobAdapter = JobAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = jobAdapter

        // Quan sát dữ liệu công việc từ ViewModel
        jobViewModel.jobs.observe(this) { jobs ->
            jobAdapter.updateList(jobs)
        }

        // Xử lý tìm kiếm
        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchBar.text.toString()
                if (query.isNotEmpty()) {
                    val intent = Intent(this, SearchActivity::class.java)
                    intent.putExtra("searchQuery", query)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }

        // Thay đổi banner
        handler = Handler()
        startBannerRotation()
    }

    // Bắt đầu luân phiên banner
    private fun startBannerRotation() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                changeBanner()
                handler.postDelayed(this, 2000)
            }
        }, 2000)
    }

    // Đổi banner
    private fun changeBanner() {
        binding.banner1.visibility = android.view.View.GONE
        binding.bnhai.visibility = android.view.View.GONE
        binding.banner3.visibility = android.view.View.GONE

        when (bannerIndex) {
            0 -> binding.banner1.visibility = android.view.View.VISIBLE
            1 -> binding.bnhai.visibility = android.view.View.VISIBLE
            2 -> binding.banner3.visibility = android.view.View.VISIBLE
        }

        bannerIndex = (bannerIndex + 1) % 3
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
