package com.example.cktimviec

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cktimviec.data.Job
import com.example.cktimviec.databinding.ItemJobBinding

class JobAdapter(private var jobList: List<Job>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            binding.tvTitle.text = job.title
            binding.tvCompany.text = job.company
            binding.tvLocation.text = job.location
            binding.tvSalary.text = "Lương: ${job.salary} VND"

            val imageUrl = job.imageUrl ?: ""
            Glide.with(binding.root.context)
                .load(if (imageUrl.isNotEmpty()) imageUrl else R.drawable.congty)
                .placeholder(android.R.drawable.ic_menu_camera)
                .error(android.R.drawable.ic_dialog_alert)
                .into(binding.ivJobAvatar)

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, JobDetailActivity::class.java)
                intent.putExtra("job_data", job)
                context.startActivity(intent)
            }
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
