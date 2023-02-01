package com.hcl.videocall.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hcl.videocall.R
import com.hcl.videocall.databinding.ActivityVideoCallBinding
import com.hcl.videocall.util.CallType
import com.hcl.videocall.util.WebRTCUtil


class VideoCallActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding : ActivityVideoCallBinding
    private val url = "file:android_asset/call.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        when(checkPermission()){
            true -> {
                init()
                initCtrl()
            }
            false ->{
                launcher.launch(arrayOf(Manifest.permission.CAMERA,
                                        Manifest.permission.RECORD_AUDIO))
            }
        }
    }

    private fun checkPermission() : Boolean{
        return ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }


    private fun init() {
        NotificationManagerCompat.from(this).cancelAll()

        binding.webview.loadUrl(url)
        binding.tvRoomId.text = "${intent?.getStringExtra("roomId")?:""}  \n ${intent?.getSerializableExtra("type") as CallType}"
        WebRTCUtil.apply {
            roomId =  intent?.getStringExtra("roomId")?:""
            callType = intent?.getSerializableExtra("type") as CallType
            setupWebView(binding.webview)
        }
    }
    private fun initCtrl(){
        binding.apply {
            btnMute.setOnCheckedChangeListener(this@VideoCallActivity)
            btnVideo.setOnCheckedChangeListener(this@VideoCallActivity)
            btnCall.setOnCheckedChangeListener(this@VideoCallActivity)
        }
    }



    override fun onCheckedChanged(view: CompoundButton?, isChecked: Boolean) {
        when(view?.id) {
            R.id.btnMute -> { WebRTCUtil.callJavaScriptFunction(binding.webview, "javascript:toggleAudio(\"$isChecked\")") }
            R.id.btnVideo -> { WebRTCUtil.callJavaScriptFunction(binding.webview, "javascript:toggleVideo(\"$isChecked\")") }
            R.id.btnCall -> { WebRTCUtil.callJavaScriptFunction(binding.webview, "javascript:disconnectCall()")
            finish()
            }
        }
    }


    private val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { permission ->
       var granted = true
        permission.entries.forEach {
           if(!it.value) granted = false
        }

        when(granted){
            true -> { init()
                      initCtrl() }
            false ->{ Toast.makeText(this,"Please allow permission",Toast.LENGTH_LONG).show() }
        }
    }
}