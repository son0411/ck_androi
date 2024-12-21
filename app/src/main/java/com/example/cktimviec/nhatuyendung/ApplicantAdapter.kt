package com.example.cktimviec.nhatuyendung

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cktimviec.databinding.ItemApplicantBinding
import com.example.cktimviec.data.Applicant

class ApplicantAdapter(private val applicants: List<Applicant>) :
    RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder>() {

    inner class ApplicantViewHolder(val binding: ItemApplicantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(applicant: Applicant) {
            binding.tvName.text = applicant.name
            binding.tvPosition.text = applicant.position
            binding.tvContact.text = applicant.contact

            // Nếu có URL CV, hiển thị và thêm chức năng mở CV
            if (applicant.cvUrl != null) {
                binding.tvCv.visibility = android.view.View.VISIBLE
                binding.tvCv.text = "Xem CV"
                binding.tvCv.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(applicant.cvUrl))
                    itemView.context.startActivity(intent)
                }
            } else {
                binding.tvCv.visibility = android.view.View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val binding = ItemApplicantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplicantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        holder.bind(applicants[position])
    }

    override fun getItemCount() = applicants.size
}
