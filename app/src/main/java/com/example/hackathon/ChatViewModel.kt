package com.example.hackathon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {
    private val _chatMessages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatMessages: StateFlow<List<Pair<String, String>>> get() = _chatMessages

    fun sendMessage(message: String) {
        // Add user's message to the chat
        addMessage("You", message)

        // Simulate sending a query to a chat bot API
        sendChatQuery(message)
    }

    private fun addMessage(sender: String, message: String) {
        _chatMessages.value += Pair(sender, message)
    }

    private fun sendChatQuery(query: String) {
        val request = ChatRequest(query)
        val call = RetrofitClient.apiService.sendQuery(request)

        call.enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                val botResponse = if (response.isSuccessful) {
                    response.body()?.answer ?: "No response from server"
                } else {
                    "Error: ${response.errorBody()?.string()}"
                }
                addMessage("Bot", botResponse)
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                val errorMessage = "Failed: ${t.message}"
                addMessage("Bot", errorMessage)
            }
        })
    }
}