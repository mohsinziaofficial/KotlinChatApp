package com.mohsin.kotlinchatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mohsin.kotlinchatapplication.databinding.ProfileItemBinding

class UserAdapter(var context : Context, var userList : ArrayList<User>): RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ProfileItemBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bindData(user: User) {
            binding.apply {
                username.text = user.name
                Glide.with(context)
                    .load(user.profileImg)
                    .placeholder(R.drawable.avatar)
                    .into(profile)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding = ProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var user = userList[position]
        holder.bindData(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}