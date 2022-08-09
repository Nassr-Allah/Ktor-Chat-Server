package com.example.plugins

import com.example.chat.DirectMessagesController
import com.example.chat.RoomController
import com.example.routes.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    val directMessagesController by inject<DirectMessagesController>()
    install(Routing) {
        chatSocket(roomController)
        displayAllMessages(roomController)
        conversation(directMessagesController)
        directMessages(directMessagesController)
    }
}
