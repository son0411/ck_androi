package com.example.cktimviec

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cktimviec.ChatAdapter
import com.example.cktimviec.data.ChatMessage
import android.content.Intent
class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    private var userExperience: String? = null
    private var desiredJob: String? = null
    private var jobLocation: String? = null
    private var currentQuestionIndex = 0

    private val questions = listOf(
        "Vui lòng nhập số năm kinh nghiệm làm việc của bạn.",
        "Công việc mong muốn của bạn là gì?",
        "Bạn muốn làm việc ở đâu?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance()
        recyclerView = findViewById(R.id.chatRecyclerView)
        chatInput = findViewById(R.id.chatInput)
        sendButton = findViewById(R.id.sendButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val userMessage = chatInput.text.toString()
            if (userMessage.isNotEmpty()) {
                sendUserMessage(userMessage)
            }
        }

        // Gửi câu hỏi đầu tiên
        sendBotMessage(questions[currentQuestionIndex])
    }

    private fun sendUserMessage(message: String) {
        val userMessage = ChatMessage(message, "user")
        chatMessages.add(userMessage)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        recyclerView.scrollToPosition(chatMessages.size - 1)

        // Xử lý câu trả lời từ người dùng
        when (currentQuestionIndex) {
            0 -> userExperience = message
            1 -> desiredJob = message
            2 -> jobLocation = message
        }

        // Chuyển sang câu hỏi tiếp theo
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            sendBotMessage(questions[currentQuestionIndex])
        } else {
            // Lưu dữ liệu vào Firestore sau khi hỏi xong
            saveUserData()
        }

        chatInput.text.clear()
    }

    private fun sendBotMessage(message: String) {
        val botMessage = ChatMessage(message, "bot")
        chatMessages.add(botMessage)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        recyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun saveUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userData = mapOf(
                "experience" to userExperience,
                "desiredJob" to desiredJob,
                "jobLocation" to jobLocation
            )
            firestore.collection("users").document(userId)
                .update(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dữ liệu đã được lưu thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi khi lưu dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
