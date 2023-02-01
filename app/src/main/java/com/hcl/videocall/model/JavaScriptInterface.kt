package com.hcl.videocall.model

import android.util.Log
import android.webkit.JavascriptInterface

class JavaScriptInterface() {
    @JavascriptInterface
    fun onPeerConnected() {
        Log.i("onPeerConnected","true")
    }
}
