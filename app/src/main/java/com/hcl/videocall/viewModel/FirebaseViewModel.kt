package com.hcl.videocall.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hcl.videocall.model.FCMPayload
import com.hcl.videocall.model.Success
import com.hcl.videocall.repository.FCM
import com.hcl.videocall.repository.FCMClient
import com.hcl.videocall.repository.FCMConstants.refreshJwtToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseViewModel : ViewModel() {


    private val apiFCM : FCM by lazy { FCMClient.getApiClient() }

    var response = MutableLiveData<Success>()
    var error = MutableLiveData<Throwable>()

    private var compositeDisposable: CompositeDisposable?= CompositeDisposable()


    fun sendNotification(data : FCMPayload?){
        apiFCM.sendNotification(data).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            response.value = it
        },{
            when(it.message?.trim().equals("HTTP 401",true)){
                true -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        refreshJwtToken()
                        sendNotification(data)
                    }
                }

                else -> { error.value = it }
            }
        }).let{ compositeDisposable?.add(it)}
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable?.dispose()
    }
}