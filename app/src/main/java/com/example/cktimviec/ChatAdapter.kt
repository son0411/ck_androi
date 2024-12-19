package com.example.cktimviec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cktimviec.databinding.ItemChatBinding
import com.example.cktimviec.data.ChatMessage
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.widget.LinearLayout

class ChatAdapter(private val chatMessages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = chatMessages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = chatMessages.size

    inner class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.messageText.text = message.message

            if (message.sender == "user") {
                // Hình đại diện và căn chỉnh tin nhắn cho người dùng
                binding.avatar.setImageResource(R.drawable.dk) // Hình đại diện người dùng
                binding.messageText.setBackgroundResource(R.drawable.message_bubble) // Thêm background tùy chỉnh cho người dùng nếu cần
                binding.messageText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black)) // Màu chữ người dùng
                (binding.root as LinearLayout).gravity = Gravity.END // Căn chỉnh tin nhắn về bên phải
            } else {
                // Hình đại diện và căn chỉnh tin nhắn cho bot
                binding.avatar.setImageResource(R.drawable.bot) // Hình đại diện bot
                binding.messageText.setBackgroundResource(R.drawable.message_bubble) // Thêm background tùy chỉnh cho bot nếu cần
                binding.messageText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black)) // Màu chữ bot
                (binding.root as LinearLayout).gravity = Gravity.START // Căn chỉnh tin nhắn về bên trái
            }
        }
    }
}
