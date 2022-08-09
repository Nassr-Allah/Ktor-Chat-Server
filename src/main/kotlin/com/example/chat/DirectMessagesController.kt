package com.example.chat

import com.example.data.data_source.MessageDataSource
import com.example.data.model.Conversation
import com.example.data.model.Member
import com.example.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class DirectMessagesController(
    private val messageDataSource: MessageDataSource
) {

    suspend fun onAccepted(username1: String, username2: String) {
        val conversation = Conversation(
            username1 = username1,
            username2 = username2,
        )
        messageDataSource.createConversation(conversation)
    }

    suspend fun sendMessage(username: String, message: String, socket: WebSocketSession, convId: String) {
        val messageEntity = Message(
            username = username,
            text = message,
            timestamp = System.currentTimeMillis(),
            conversationId = convId
        )
        messageDataSource.insertMessage(messageEntity)

        val parsedMessage = Json.encodeToString(messageEntity)
        socket.send(Frame.Text(parsedMessage))
    }

    suspend fun displayConversationMessages(conversationId: String): List<Message> {
        return messageDataSource.getConversationMessages(conversationId)
    }

    suspend fun getUserConversations(username: String): List<Conversation> {
        return messageDataSource.getUserConversations(username)
    }

}