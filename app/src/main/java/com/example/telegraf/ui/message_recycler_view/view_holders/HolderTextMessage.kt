package com.example.telegraf.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.UID
import com.example.telegraf.ui.message_recycler_view.views.MessageView
import com.example.telegraf.utilities.asTime

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {
    //user message
    val blockUserMessage = view.findViewById<ConstraintLayout>(R.id.block_user_message);
    val chatUserMessage = view.findViewById<TextView>(R.id.chat_user_message);
    val chatUserMessageTime = view.findViewById<TextView>(R.id.chat_user_time);
    //received message
    val blockReceivedMessage = view.findViewById<ConstraintLayout>(R.id.block_receive_message);
    val chatReceivedMessage = view.findViewById<TextView>(R.id.chat_receive_message);
    val chatReceivedMessageTime = view.findViewById<TextView>(R.id.chat_receive_time);

    override fun drawMessage(messageView: MessageView){

        if (messageView.from == UID) {
            blockUserMessage.visibility = View.VISIBLE;
            blockReceivedMessage.visibility = View.GONE;
            chatUserMessage.text = messageView.text;
            chatUserMessageTime.text = messageView.timeStamp.toString().asTime()
        } else {
            blockUserMessage.visibility = View.GONE;
            blockReceivedMessage.visibility = View.VISIBLE;
            chatReceivedMessage.text = messageView.text;
            chatReceivedMessageTime.text = messageView.timeStamp.toString().asTime()
        }
    }

    override fun attach(messageView: MessageView) {
    }

    override fun detach() {
    }
}