package com.example.telegraf.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.models.CommonModel
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.asTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SingleChatAdapter(): RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var chatList: List<CommonModel> = emptyList();

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false);
        return SingleChatHolder(view);
    }
    override fun getItemCount(): Int{
        return chatList.size;
    }
    override fun onBindViewHolder(holder: SingleChatHolder, position: Int){
        if(chatList[position].from == UID){
            holder.blockUserMessage.visibility = View.VISIBLE;
            holder.blockReceivedMessage.visibility = View.GONE;
            holder.chatUserMessage.text = chatList[position].text;
            holder.chatUserMessageTime.text = chatList[position].timeStamp.toString().asTime()
        }else{
            holder.blockUserMessage.visibility = View.GONE;
            holder.blockReceivedMessage.visibility = View.VISIBLE;
            holder.chatReceivedMessage.text = chatList[position].text;
            holder.chatReceivedMessageTime.text = chatList[position].timeStamp.toString().asTime()
        }
    }
    fun setList(list: List<CommonModel>){
        chatList = list;
        this.notifyDataSetChanged()
    }

    class SingleChatHolder(val view: View): RecyclerView.ViewHolder(view){
        val blockUserMessage = view.findViewById<ConstraintLayout>(R.id.block_user_message);
        val chatUserMessage = view.findViewById<TextView>(R.id.chat_user_message);
        val chatUserMessageTime = view.findViewById<TextView>(R.id.chat_user_time);

        val blockReceivedMessage = view.findViewById<ConstraintLayout>(R.id.block_receive_message);
        val chatReceivedMessage = view.findViewById<TextView>(R.id.chat_receive_message);
        val chatReceivedMessageTime = view.findViewById<TextView>(R.id.chat_receive_time);
    }
}


