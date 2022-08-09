package com.example.chat

import com.example.data.data_source.MessageDataSource
import com.example.data.model.Member
import com.example.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {

    private val members = ConcurrentHashMap<String,Member>()

    fun onJoin(username: String, sessionId: String, socket: WebSocketSession) {
        members[username] = Member(username, sessionId, socket)
    }

    suspend fun sendMessage(username: String, message: String) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                username = username,
                timestamp = System.currentTimeMillis()
            )
            messageDataSource.insertMessage(messageEntity)

            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun displayAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun disconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }

}