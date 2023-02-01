package com.hcl.videocall.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.hcl.videocall.databinding.ActivityMainBinding
import com.hcl.videocall.model.FCMPayload
import com.hcl.videocall.model.Message
import com.hcl.videocall.model.Notification
import com.hcl.videocall.model.UserModel
import com.hcl.videocall.util.CallType
import com.hcl.videocall.util.WebRTCUtil
import com.hcl.videocall.viewModel.FirebaseViewModel

class MainActivity : AppCompatActivity(), OnCompleteListener<String>, ValueEventListener, (UserModel?) -> Unit {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : FirebaseViewModel
    private lateinit var reference : DatabaseReference
    private var roomId : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        init()
        initCtrl()
    }


    private fun init() {
        viewModel = ViewModelProviders.of(this).get(FirebaseViewModel::class.java)
        reference = Firebase.database.getReference("Users")


        viewModel.apply {
            response.observe(this@MainActivity) {
                Log.e("times","times")
                val intent = Intent(this@MainActivity,VideoCallActivity::class.java)
                intent.putExtra("roomId", roomId)
                intent.putExtra("type", CallType.CREATE)
                startActivity(intent)
            }
            error.observe(this@MainActivity){
              Toast.makeText(this@MainActivity,it.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initCtrl(){
        requestToken()
        reference.addListenerForSingleValueEvent(this)
        reference.addValueEventListener(this)
    }

    private fun requestToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(this)
    }

    override fun onComplete(task: Task<String>) {
        when(task.isSuccessful){
            true -> {
                reference.child(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)).setValue(UserModel(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
                                Settings.Secure.getString(contentResolver,"bluetooth_name"),
                                task.result))
            }
            false  -> { requestToken() }
        }
    }


    override fun onDataChange(snapshot: DataSnapshot) {
        val list = arrayListOf<UserModel?>()
        for (snapshot in snapshot.children) {
            val model = snapshot.getValue(UserModel::class.java)
            when(model?.deviceId){
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ->{}
                else -> { list.add(snapshot.getValue(UserModel::class.java)) }
            }
        }

        binding.progress.visibility = View.GONE

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = UserAdapter(list,this)

    }

    override fun onCancelled(error: DatabaseError) {
        Toast.makeText(this,error.message,Toast.LENGTH_LONG).show()
    }

    override fun invoke(data : UserModel?) {
         data?.roomId = WebRTCUtil.getUniqueId()
         roomId = data?.roomId?:""

        Log.e("sendRoomID",roomId)
        viewModel.sendNotification(FCMPayload(Message(Notification("${Settings.Secure.getString(contentResolver,"bluetooth_name")} calling you ","Incoming Call"),data?.token?:"",data)))
    }

}