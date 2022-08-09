package com.example.di

import com.example.chat.DirectMessagesController
import com.example.chat.RoomController
import com.example.data.data_source.MessageDataSource
import com.example.data.data_source.MessageDataSourceImpl
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient().coroutine.getDatabase("chat_db")
    }
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single {
        RoomController(get())
    }
    single {
        DirectMessagesController(get())
    }
}