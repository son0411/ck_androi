package com.example.cktimviec

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
        searchJobs(query, null)

        // Xử lý khi nhấn vào icon "more" để chọn tỉnh thành
        binding.locationDropdown.setOnClickListener {
            showLocationPopup()
        }
    }

    private fun showLocationPopup() {
        val popupMenu = PopupMenu(this, binding.locationDropdown)
        val locations = listOf("Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Bình Dương")

        // Tạo menu từ danh sách tỉnh thành
        locations.forEach { location ->
            popupMenu.menu.add(location)
        }

        // Xử lý khi chọn tỉnh thành
        popupMenu.setOnMenuItemClickListener { menuItem ->
            selectedLocation = menuItem.title.toString()
            binding.locationText.text = selectedLocation
            searchJobs(null, selectedLocation) // Lọc công việc theo tỉnh thành
            true
        }

        popupMenu.show()
    }

    private fun searchJobs(query: String?, location: String?) {
        val queryRef = firestore.collection("jobs") // CollectionReference

        // Áp dụng bộ lọc dựa trên từ khóa và tỉnh thành
        var filteredQuery: Query = queryRef
        if (!query.isNullOrEmpty()) {
            filteredQuery = filteredQuery
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
        }

        if (!location.isNullOrEmpty()) {
            filteredQuery = filteredQuery.whereEqualTo("location", location)
        }

        // Thực hiện truy vấn
        filteredQuery.get()
            .addOnSuccessListener { documents ->
                val jobList = documents.map { doc ->
                    Job(
                        title = doc.getString("title") ?: "",
                        company = doc.getString("company") ?: "",
                        location = doc.getString("location") ?: "",
                        salary = doc.getString("salary") ?: ""
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

    private fun updateResultCount(count: Int) {
        binding.resultCount.text = "$count kết quả"
    }
}
