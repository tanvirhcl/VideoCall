package com.hcl.videocall.model

data class Message(
    val notification: Notification,
    val token: String,
    val data : UserModel?
)