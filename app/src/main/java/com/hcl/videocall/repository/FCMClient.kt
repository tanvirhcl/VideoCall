package com.hcl.videocall.repository

import android.content.Context.MODE_PRIVATE
import com.google.gson.GsonBuilder
import com.hcl.videocall.repository.FCMConstants.APPLICATION_NAME
import com.hcl.videocall.repository.FCMConstants.BASE_URL
import com.hcl.videocall.repository.FCMConstants.JWT_TOKEN
import com.hcl.videocall.repository.FCMConstants.refreshJwtToken
import com.hcl.videocall.base.BaseApplication.Companion.APPLICATION
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object FCMClient {

    fun getApiClient(baseUrl: String = BASE_URL, connectTimeoutInSec: Long = 30, readTimeoutInSec: Long = 50, writeTimeoutInSec: Long = 60): FCM {
        return getRetrofitClient(getOkhttpClientBuilder(connectTimeoutInSec, readTimeoutInSec, writeTimeoutInSec), baseUrl).create(FCM::class.java)
    }
     fun getRetrofitClient(okHttpClientBuilder: OkHttpClient.Builder, baseUrl: String): Retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttpClientBuilder.build())
        .baseUrl(baseUrl)
        .build()
     fun getOkhttpClientBuilder(connectTimeoutInSec: Long, readTimeoutInSec: Long, writeTimeoutInSec: Long): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.connectTimeout(connectTimeoutInSec, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(readTimeoutInSec, TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(writeTimeoutInSec, TimeUnit.SECONDS)
        okHttpClientBuilder.addInterceptor(Interceptor { chain ->
            val jwtToken = APPLICATION.getSharedPreferences(APPLICATION_NAME,MODE_PRIVATE).getString(JWT_TOKEN,"")
            if(jwtToken?.isEmpty()==true) refreshJwtToken()
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .header("Authorization", "Bearer ${APPLICATION.getSharedPreferences(APPLICATION_NAME,MODE_PRIVATE).getString(JWT_TOKEN,"")}")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
        return okHttpClientBuilder
    }


}