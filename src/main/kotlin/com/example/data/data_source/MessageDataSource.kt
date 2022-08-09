package com.example.data.data_source

import com.example.data.model.Conversation
import com.example.data.model.Message

interface MessageDataSource {

    suspend fun getAllMessages(): List<Message>

    suspend fun insertMessage(message: Message)

    suspend fun createConversation(conversation: Conversation)

    suspend fun getConversationMessages(conversationId: String): List<Message>

    suspend fun getUserConversations(username: String): List<Conversation>

}