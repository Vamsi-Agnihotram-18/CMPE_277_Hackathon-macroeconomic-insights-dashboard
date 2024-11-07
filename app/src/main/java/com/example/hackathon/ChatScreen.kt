package com.example.hackathon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel()) {
    val chatMessages by chatViewModel.chatMessages.collectAsState()
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chat Prompt",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (chatMessages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Welcome to Chat Prompt", fontSize = 22.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                reverseLayout = false
            ) {
                itemsIndexed(chatMessages) { _, (sender, text) ->
                    if (sender == "You") {
                        UserMessage(message = text)
                    } else {
                        BotMessage(message = text)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .height(56.dp),
                placeholder = { Text("type a new question here") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                    focusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.3f),
                    focusedPlaceholderColor = Color.Gray.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )
            IconButton(onClick = {
                if (message.isNotBlank()) {
                    chatViewModel.sendMessage(message)
                    message = ""
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
            }
        }
    }
}

@Composable
fun UserMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .wrapContentSize(Alignment.CenterEnd)
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFF128C7E), shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        )
    }
}

@Composable
fun BotMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(horizontal = 8.dp)
            .wrapContentSize(Alignment.CenterStart)
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier
                .background(Color(0xFFECECEC), shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        )
    }
}