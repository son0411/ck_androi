package com.example.cktimviec.nhatuyendung

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cktimviec.databinding.ItemJobBinding
import com.example.cktimviec.data.Job

class JobAdapter(
    private val jobs: List<Job>,
    private val onItemClick: (Job) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            binding.tvTitle.text = job.title
            binding.tvCompany.text = job.company
            binding.tvLocation.text = job.location

            // Sử dụng Glide để tải hình ảnh vào ImageView
            Glide.with(binding.ivJobAvatar.context)
                .load(job.imageUrl) // Giả sử bạn có URL hình ảnh trong đối tượng Job
                .into(binding.ivJobAvatar)

            binding.root.setOnClickListener {
                // Truyền công việc vào JobDetailActivity
                onItemClick(job)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(jobs[position])
    }

    override fun getItemCount() = jobs.size
}
