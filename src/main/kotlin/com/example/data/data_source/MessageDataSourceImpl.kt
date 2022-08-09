package com.example.data.data_source

import com.example.data.model.Conversation
import com.example.data.model.Message
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MessageDataSourceImpl(
    private val dbInstance: CoroutineDatabase
) : MessageDataSource {

    private val messages = dbInstance.getCollection<Message>()
    private val conversations = dbInstance.getCollection<Conversation>()

    override suspend fun getAllMessages(): List<Message> {
        return messages.find().descendingSort(Message::timestamp).toList()
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun createConversation(conversation: Conversation) {
        val conv = conversations.findOne(
            and(
                Conversation::username1 eq conversation.username1,
                Conversation::username2 eq conversation.username2
            )
        )
        if (conv == null) {
            conversations.insertOne(conversation)
        }
    }

    override suspend fun getConversationMessages(conversationId: String): List<Message> {
        return messages.find(conversationId).descendingSort(Message::timestamp).toList()
    }

    override suspend fun getUserConversations(username: String): List<Conversation> {
        return conversations.find(username).toList()
    }
}