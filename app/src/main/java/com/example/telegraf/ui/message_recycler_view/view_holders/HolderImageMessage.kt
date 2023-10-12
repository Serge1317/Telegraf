package com.example.telegraf.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.UID
import com.example.telegraf.ui.message_recycler_view.views.MessageView
import com.example.telegraf.utilities.asTime
import com.example.telegraf.utilities.downloadAndSetImage

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {
    //user message
    val blockUserImage = view.findViewById<ConstraintLayout>(R.id.block_user_image);
    val chatUserImage = view.findViewById<ImageView>(R.id.chat_user_image);
    val chatUserMessageTimeImage = view.findViewById<TextView>(R.id.chat_user_time_image);
    //received message
    val blockReceivedImage = view.findViewById<ConstraintLayout>(R.id.block_receive_image);
    val chatReceivedImage = view.findViewById<ImageView>(R.id.chat_receive_image);
    val chatReceivedMessageTimeImage = view.findViewById<TextView>(R.id.chat_receive_time_image);

    override fun drawMessage(messageView: MessageView){

        if (messageView.from == UID) {
            blockUserImage.visibility = View.VISIBLE;
            blockReceivedImage.visibility = View.GONE;
            chatUserImage.downloadAndSetImage(messageView.fileUrl)
            chatUserMessageTimeImage.text = messageView.timeStamp.toString().asTime()
        } else {
            blockUserImage.visibility = View.GONE;
            blockReceivedImage.visibility = View.VISIBLE;
            chatReceivedImage.downloadAndSetImage(messageView.fileUrl)
            chatReceivedMessageTimeImage.text = messageView.timeStamp.toString().asTime()
        }
    }

    override fun attach(messageView: MessageView) {
    }

    override fun detach() {
    }
}