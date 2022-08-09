package com.example.data.model

import io.ktor.websocket.*
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Conversation(
    @BsonId
    val id: String = ObjectId().toString(),
    val username1: String,
    val username2: String,
)
