package com.example.cktimviec

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cktimviec.data.Job
import com.example.cktimviec.databinding.ActivitySearchBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var jobAdapter: JobAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private var selectedLocation: String? = null
    private var selectedSalaryRange: Pair<Long, Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy từ khóa tìm kiếm từ Intent
        val query = intent.getStringExtra("searchQuery") ?: ""

        // Thiết lập RecyclerView
        jobAdapter = JobAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = jobAdapter

        // Lọc công việc ban đầu (theo từ khóa nếu có)
        searchJobs(query, selectedLocation, selectedSalaryRange)

        // Xử lý khi nhấn vào icon "more" để chọn tỉnh thành
        binding.locationDropdown.setOnClickListener {
            showLocationPopup()
        }

        // Xử lý khi nhấn vào nút "Lọc mức lương"
        binding.salaryFilterUSD.setOnClickListener {
            showSalaryPopup()
        }

        // Xử lý khi nhấn vào nút "Lọc kinh nghiệm"
        binding.experienceFilter.setOnClickListener {
            showExperiencePopup()
        }

        // Xử lý khi nhấn vào nút tìm kiếm
        binding.filterButton.setOnClickListener {
            val searchQuery = binding.searchBar.text.toString()
            searchJobs(searchQuery, selectedLocation, selectedSalaryRange)
        }
    }

    // Hiển thị popup cho việc chọn tỉnh thành
    private fun showLocationPopup() {
        val popupMenu = PopupMenu(this, binding.locationDropdown)
        val locations = listOf("Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Bình Dương")

        locations.forEach { location ->
            popupMenu.menu.add(location)
        }

        // Xử lý khi chọn tỉnh thành
        popupMenu.setOnMenuItemClickListener { menuItem ->
            selectedLocation = menuItem.title.toString()
            binding.locationText.text = selectedLocation
            searchJobs(null, selectedLocation, selectedSalaryRange) // Lọc công việc theo tỉnh thành và lương
            true
        }

        popupMenu.show()
    }

    // Hiển thị popup cho việc chọn mức lương
    private fun showSalaryPopup() {
        val popupMenu = PopupMenu(this, binding.salaryFilterUSD)
        val salaryRanges = listOf("0-1000 USD", "1000-5000 USD", "5000-10000 USD", "10000+ USD")

        salaryRanges.forEach { range ->
            popupMenu.menu.add(range)
        }

        // Xử lý khi chọn mức lương
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedRange = menuItem.title.toString()
            selectedSalaryRange = parseSalaryRange(selectedRange)
            searchJobs(null, selectedLocation, selectedSalaryRange) // Lọc công việc theo mức lương và tỉnh thành
            true
        }

        popupMenu.show()
    }

    // Hiển thị popup cho việc chọn kinh nghiệm
    private fun showExperiencePopup() {
        val popupMenu = PopupMenu(this, binding.experienceFilter)
        val experienceLevels = listOf("Không yêu cầu", "1-3 năm", "3-5 năm", "5+ năm")

        experienceLevels.forEach { level ->
            popupMenu.menu.add(level)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedExperience = menuItem.title.toString()
            binding.experienceFilter.text = selectedExperience
            // Lọc công việc theo kinh nghiệm nếu cần
            searchJobs(null, selectedLocation, selectedSalaryRange) // Tùy chỉnh lọc theo kinh nghiệm nếu cần
            true
        }

        popupMenu.show()
    }

    // Hàm phân tích dải mức lương từ chuỗi
    private fun parseSalaryRange(range: String): Pair<Long, Long>? {
        return when (range) {
            "0-1000 USD" -> Pair(0L, 1000L)
            "1000-5000 USD" -> Pair(1000L, 5000L)
            "5000-10000 USD" -> Pair(5000L, 10000L)
            "10000+ USD" -> Pair(10000L, Long.MAX_VALUE)
            else -> null
        }
    }

    // Hàm tìm kiếm công việc với bộ lọc theo từ khóa, tỉnh thành và mức lương
    private fun searchJobs(query: String?, location: String?, salaryRange: Pair<Long, Long>?) {
        val queryRef = firestore.collection("jobs")

        var filteredQuery: Query = queryRef
        if (!query.isNullOrEmpty()) {
            filteredQuery = filteredQuery
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
        }

        if (!location.isNullOrEmpty()) {
            filteredQuery = filteredQuery.whereEqualTo("location", location)
        }

        if (salaryRange != null) {
            filteredQuery = filteredQuery
                .whereGreaterThanOrEqualTo("salary", salaryRange.first)
                .whereLessThanOrEqualTo("salary", salaryRange.second)
        }

        filteredQuery.get()
            .addOnSuccessListener { documents ->
                val jobList = documents.map { doc ->
                    val salary = doc.getLong("salary") ?: 0L

                    Job(
                        title = doc.getString("title") ?: "",
                        company = doc.getString("company") ?: "",
                        location = doc.getString("location") ?: "",
                        salary = salary
                    )
                }
                jobAdapter.updateList(jobList)
                updateResultCount(jobList.size)
            }
            .addOnFailureListener {
                jobAdapter.updateList(emptyList())
                updateResultCount(0)
            }
    }

    // Cập nhật số lượng kết quả tìm được
    private fun updateResultCount(count: Int) {
        binding.resultCount.text = "$count kết quả"
    }
}
