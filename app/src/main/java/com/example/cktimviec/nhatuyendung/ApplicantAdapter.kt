package com.example.cktimviec.nhatuyendung

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cktimviec.R
import com.example.cktimviec.data.Applicant
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ApplicantAdapter(private val applicants: List<Applicant>) :
    RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder>() {

    inner class ApplicantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        val tvJobTitle: TextView = itemView.findViewById(R.id.tv_job_title)
        val tvJobCompany: TextView = itemView.findViewById(R.id.tv_job_company)
        val btnViewCv: Button = itemView.findViewById(R.id.btn_view_cv)
        val btnContact: Button = itemView.findViewById(R.id.btn_contact) // Nút liên hệ
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_applicant, parent, false)
        return ApplicantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        val applicant = applicants[position]

        // Gán dữ liệu cho TextView
        holder.tvName.text = applicant.name
        holder.tvEmail.text = applicant.email
        holder.tvJobTitle.text = applicant.jobTitle
        holder.tvJobCompany.text = applicant.jobCompany

        // Xử lý khi bấm nút xem CV
        holder.btnViewCv.setOnClickListener {
            if (applicant.cvUrl.isNotEmpty()) {
                openCv(holder.itemView.context, applicant.cvUrl)
            } else {
                Toast.makeText(holder.itemView.context, "Ứng viên chưa tải lên CV", Toast.LENGTH_SHORT).show()
            }
        }

        // Xử lý khi bấm nút liên hệ
        // Thêm nút "Liên hệ" trong item_applicant.xml (nếu chưa có)
        holder.btnContact.setOnClickListener {
            val intent = Intent(holder.itemView.context, ContactAdminActivity::class.java)
            intent.putExtra("APPLICANT_ID", applicant.id) // Truyền ID ứng viên
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = applicants.size

    private fun openCv(context: Context, cvUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cvUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở CV", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNotification(context: Context, userId: String) {
        val firestore = FirebaseFirestore.getInstance()

        val notificationData = mapOf(
            "title" to "Yêu cầu liên hệ",
            "message" to "Nhà tuyển dụng muốn liên hệ với bạn!",
            "userId" to userId,
            "timestamp" to Date() // Lưu thời gian gửi
        )

        firestore.collection("notifications")
            .add(notificationData)
            .addOnSuccessListener {
                Toast.makeText(context, "Thông báo đã được lưu!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Lỗi khi lưu thông báo!", Toast.LENGTH_SHORT).show()
            }
    }
}
