package com.example.routes

import com.example.chat.DirectMessagesController
import com.example.chat.RoomController
import com.example.session.ChatSession
import com.example.session.DMChatSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocket(roomController: RoomController) {
    webSocket("/chat") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid Session"))
            return@webSocket
        }
        try {
            roomController.onJoin(session.username, session.sessionId, this)
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    roomController.sendMessage(session.username, frame.readText())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.disconnect(session.username)
        }
    }
}

fun Route.displayAllMessages(roomController: RoomController) {
    get("/messages") {
        call.respond(
            HttpStatusCode.OK,
            roomController.displayAllMessages()
        )
    }
}

fun Route.directMessages(directMessagesController: DirectMessagesController) {
    webSocket("chat/{id?}") {
        val session = call.sessions.get<ChatSession>()
        val conversationId = call.parameters["id"] ?: ""
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid Session"))
            return@webSocket
        }
        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    directMessagesController.sendMessage(
                        session.username,
                        frame.readText(),
                        this,
                        conversationId
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Route.conversation(directMessagesController: DirectMessagesController) {
    post("/conversation") {
        val username1 = call.parameters["username1"] ?: "user1"
        val username2 = call.parameters["username2"] ?: "user2"
        directMessagesController.onAccepted(username1, username2)
    }
    get("/conversation") {
        val username = call.parameters["username"] ?: ""
        call.respond(
            HttpStatusCode.OK,
            directMessagesController.getUserConversations(username)
        )
    }
    get("/conversation/{?id}") {
        val convId = call.parameters["id"] ?: ""
        call.respond(
            HttpStatusCode.OK,
            directMessagesController.displayConversationMessages(convId)
        )
    }
}