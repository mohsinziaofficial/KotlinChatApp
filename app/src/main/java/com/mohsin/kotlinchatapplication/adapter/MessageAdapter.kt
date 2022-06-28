package com.mohsin.kotlinchatapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mohsin.kotlinchatapplication.R
import com.mohsin.kotlinchatapplication.databinding.DeleteLayoutBinding
import com.mohsin.kotlinchatapplication.databinding.ReceiveMsgBinding
import com.mohsin.kotlinchatapplication.databinding.SendMsgBinding
import com.mohsin.kotlinchatapplication.model.MessageData

class MessageAdapter (
    var context: Context,
    messageDataList: ArrayList<MessageData>?,
    senderRoom: String,
    receiverRoom: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    lateinit var messageDataList: ArrayList<MessageData>
    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2
    var senderRoom: String
    var receiverRoom: String

    init {
        if (messageDataList != null) {
            this.messageDataList = messageDataList
        }
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }

    override fun getItemViewType(position: Int): Int {
        val messageData: MessageData = messageDataList[position]
        // if firebase UID is equal to sender ID it means message is from sender
        return if (FirebaseAuth.getInstance().uid == messageData.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val binding = SendMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            val view: View = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SentViewHolder(binding)
        } else {
            val binding = ReceiveMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            ReceiverViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messageData: MessageData = messageDataList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            viewHolder.bindData(messageData)
        }
        else {
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.bindData(messageData)
        }
    }

    override fun getItemCount(): Int {
        return messageDataList.size
    }


    inner class SentViewHolder(private val binding : SendMsgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(messageData : MessageData) {
            binding.apply {
                if (messageData.message.equals("photo")) {
                    image.visibility = View.VISIBLE
                    message.visibility = View.GONE
                    mLinear.visibility = View.GONE
                    Glide.with(context)
                        .load(messageData.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(image)
                }
                message.text = messageData.message

                itemView.setOnLongClickListener {
                    val view: View = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                    val bindingDel: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)

                    val dialog: AlertDialog = AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.root)
                        .create()

                    bindingDel.apply {
                        everyone.setOnClickListener(View.OnClickListener {
                            messageData.message = "This message is removed."
                            messageData.messageId?.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(it1).setValue(message)
                            }
                            messageData.messageId?.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(it1).setValue(message)
                            }
                            dialog.dismiss()
                        })

                        delete.setOnClickListener(View.OnClickListener {
                            messageData.messageId?.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(it1).setValue(null)
                            }
                            dialog.dismiss()
                        })

                        cancel.setOnClickListener(View.OnClickListener {
                            dialog.dismiss()
                        })
                    }
                    dialog.show()
                    false
                }
            }
        }
    }

    inner class ReceiverViewHolder(private val binding : ReceiveMsgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(messageData : MessageData) {
            binding.apply {
                if (messageData.message.equals("photo")) {
                    image.visibility = View.VISIBLE
                    message.visibility = View.GONE
                    mLinear.visibility = View.GONE
                    Glide.with(context)
                        .load(messageData.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(image)
                }
                message.text = messageData.message

                itemView.setOnLongClickListener {
                    val view: View = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                    val bindingDel: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)

                    val dialog: AlertDialog = AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setView(binding.root)
                        .create()

                    bindingDel.apply {
                        everyone.setOnClickListener(View.OnClickListener {
                            messageData.message = "This message is removed."
                            messageData.messageId?.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(it1).setValue(message)
                            }
                            messageData.messageId?.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(it1).setValue(message)
                            }
                            dialog.dismiss()
                        })

                        delete.setOnClickListener(View.OnClickListener {
                            messageData.messageId?.let { it1 ->
                                FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(it1).setValue(null)
                            }
                            dialog.dismiss()
                        })

                        cancel.setOnClickListener(View.OnClickListener {
                            dialog.dismiss()
                        })
                    }
                    dialog.show()
                    false
                }
            }
        }
    }

}