package com.hcl.videocall.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hcl.videocall.databinding.AdapterUserBinding
import com.hcl.videocall.model.UserModel

class UserAdapter(var list : MutableList<UserModel?>?, var listner : ((UserModel?)-> Unit) ) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    override fun getItemCount() = list?.size?:0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(AdapterUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
    }

   inner class MyViewHolder(var binding: AdapterUserBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivCall.setOnClickListener { listner.invoke(list?.get(adapterPosition)) }
        }
        fun bind(){
            binding.tvName.text = list?.get(adapterPosition)?.deviceName

        }
    }
}