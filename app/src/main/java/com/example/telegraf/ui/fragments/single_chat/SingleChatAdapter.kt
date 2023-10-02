package com.example.telegraf.ui.fragments.single_chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.TYPE_MESSAGE_IMAGE
import com.example.telegraf.database.TYPE_MESSAGE_TEXT
import com.example.telegraf.models.CommonModel
import com.example.telegraf.database.UID
import com.example.telegraf.utilities.DiffUtilCallback
import com.example.telegraf.utilities.asTime
import com.example.telegraf.utilities.downloadAndSetImage
import java.util.concurrent.ConcurrentLinkedDeque

class SingleChatAdapter() : RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var cacheList = mutableListOf<CommonModel>();

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false);
        return SingleChatHolder(view);
    }

    override fun getItemCount(): Int {
        return cacheList.size;
    }

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when(cacheList[position].type){
            TYPE_MESSAGE_TEXT -> drawMessageText(holder, position);
            TYPE_MESSAGE_IMAGE -> drawMessageImage(holder, position);
        }
    }

    private fun drawMessageText(holder: SingleChatHolder, position: Int){
        holder.blockUserImage.visibility = View.GONE;
        holder.blockReceivedImage.visibility = View.GONE;

        if (cacheList[position].from == UID) {
            holder.blockUserMessage.visibility = View.VISIBLE;
            holder.blockReceivedMessage.visibility = View.GONE;
            holder.chatUserMessage.text = cacheList[position].text;
            holder.chatUserMessageTime.text = cacheList[position].timeStamp.toString().asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE;
            holder.blockReceivedMessage.visibility = View.VISIBLE;
            holder.chatReceivedMessage.text = cacheList[position].text;
            holder.chatReceivedMessageTime.text = cacheList[position].timeStamp.toString().asTime()
        }
    }

    private fun drawMessageImage(holder: SingleChatHolder, position: Int){
        holder.blockUserMessage.visibility = View.GONE;
        holder.blockReceivedMessage.visibility = View.GONE;

        if (cacheList[position].from == UID) {
            holder.blockUserImage.visibility = View.VISIBLE;
            holder.blockReceivedImage.visibility = View.GONE;
            holder.chatUserImage
                .downloadAndSetImage(cacheList[position].imageUrl)
            holder.chatUserMessageTimeImage.text = cacheList[position].timeStamp.toString().asTime()
        } else {
            holder.blockUserImage.visibility = View.GONE;
            holder.blockReceivedImage.visibility = View.VISIBLE;
            holder.chatReceivedImage
                .downloadAndSetImage(cacheList[position].imageUrl)
            holder.chatReceivedMessageTimeImage.text = cacheList[position].timeStamp.toString().asTime()
        }
    }
    fun addItemToBottom(item: CommonModel, onSuccess: () -> Unit){
        if ( ! cacheList.contains(item)) {
            cacheList.add(item);
            this.notifyItemInserted(cacheList.size)
        }
        onSuccess()
    }
    fun addItemToTop(item: CommonModel, onSuccess: () -> Unit){
        if ( ! cacheList.contains(item)) {
            cacheList.add(item);
            cacheList.sortBy { it.timeStamp.toString() }
            this.notifyItemInserted(0)
        }
        onSuccess();
    }

    class SingleChatHolder(val view: View) : RecyclerView.ViewHolder(view) {

        // text message
            //user message
        val blockUserMessage = view.findViewById<ConstraintLayout>(R.id.block_user_message);
        val chatUserMessage = view.findViewById<TextView>(R.id.chat_user_message);
        val chatUserMessageTime = view.findViewById<TextView>(R.id.chat_user_time);
            //received message
        val blockReceivedMessage = view.findViewById<ConstraintLayout>(R.id.block_receive_message);
        val chatReceivedMessage = view.findViewById<TextView>(R.id.chat_receive_message);
        val chatReceivedMessageTime = view.findViewById<TextView>(R.id.chat_receive_time);

        // image message
            //user message
        val blockUserImage = view.findViewById<ConstraintLayout>(R.id.block_user_image);
        val chatUserImage = view.findViewById<ImageView>(R.id.chat_user_image);
        val chatUserMessageTimeImage = view.findViewById<TextView>(R.id.chat_user_time_image);
            //received message
        val blockReceivedImage = view.findViewById<ConstraintLayout>(R.id.block_receive_image);
        val chatReceivedImage = view.findViewById<ImageView>(R.id.chat_receive_image);
        val chatReceivedMessageTimeImage = view.findViewById<TextView>(R.id.chat_receive_time_image);
    }
}


