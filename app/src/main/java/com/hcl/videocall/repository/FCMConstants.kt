package com.hcl.videocall.repository

import android.content.Context.MODE_PRIVATE
import com.google.auth.oauth2.GoogleCredentials
import com.hcl.videocall.BuildConfig
import com.hcl.videocall.base.BaseApplication.Companion.APPLICATION

object FCMConstants {

    internal const val BASE_URL = "https://fcm.googleapis.com"
    private const val FIREBASE_PROJECT_ID = "videocall-25b58"
    internal const val NOTIFICATION = "v1/projects/${FIREBASE_PROJECT_ID}/messages:send"

    private const val MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
    internal val SCOPES = arrayListOf(MESSAGING_SCOPE)

    internal const val APPLICATION_NAME = BuildConfig.APPLICATION_ID
    internal const val JWT_TOKEN = "jwtToken"


     fun refreshJwtToken() {
        val path= APPLICATION.assets.open("service-account.json")
        val googleCredentials: GoogleCredentials = GoogleCredentials
            .fromStream(path)
            .createScoped(SCOPES)
        APPLICATION.getSharedPreferences(APPLICATION_NAME, MODE_PRIVATE).edit()
                    .putString(JWT_TOKEN,googleCredentials.refreshAccessToken().tokenValue).apply()

    }

}