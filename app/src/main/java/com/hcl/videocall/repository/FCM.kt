package com.hcl.videocall.repository

import com.hcl.videocall.model.FCMPayload
import com.hcl.videocall.model.Success
import com.hcl.videocall.repository.FCMConstants.NOTIFICATION
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface FCM {

    @POST(NOTIFICATION)
    fun sendNotification(@Body body : FCMPayload?) : Observable<Success>
}