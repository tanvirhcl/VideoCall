package com.hcl.videocall.model

data class Error(
    val code: Int,
    val details: List<Detail>,
    val message: String,
    val status: String
)