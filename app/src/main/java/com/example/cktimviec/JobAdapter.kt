package com.example.cktimviec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cktimviec.data.Job
import com.example.cktimviec.databinding.ItemJobBinding
import com.bumptech.glide.Glide

class JobAdapter(private var jobList: List<Job>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            binding.tvTitle.text = job.title
            binding.tvCompany.text = job.company
            binding.tvLocation.text = job.location
            binding.tvSalary.text = job.salary.toString()

            // Kiểm tra nếu imageUrl không rỗng và sử dụng Glide để tải ảnh
            val imageUrl = job.imageUrl ?: "" // Nếu imageUrl trống, thay thế bằng chuỗi rỗng
            Glide.with(binding.root.context)
                .load(if (imageUrl.isNotEmpty()) imageUrl else R.drawable.congty) // Nếu không có URL, sử dụng ảnh mặc định
                .placeholder(android.R.drawable.ic_menu_camera) // Ảnh mặc định khi tải
                .error(android.R.drawable.ic_dialog_alert) // Ảnh lỗi khi có sự cố
                .into(binding.ivJobAvatar) // Tham chiếu tới ImageView trong ItemJobBinding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(jobList[position])
    }

    override fun getItemCount() = jobList.size

    fun updateList(newList: List<Job>) {
        jobList = newList
        notifyDataSetChanged()
    }
}
