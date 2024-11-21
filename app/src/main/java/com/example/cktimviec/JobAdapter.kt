package com.example.cktimviec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cktimviec.data.Job
import com.example.cktimviec.databinding.ItemJobBinding

class JobAdapter(private var jobList: List<Job>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            binding.tvTitle.text = job.title
            binding.tvCompany.text = job.company
            binding.tvLocation.text = job.location
            binding.tvSalary.text = job.salary
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
