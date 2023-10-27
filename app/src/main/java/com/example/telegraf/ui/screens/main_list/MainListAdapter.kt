package com.example.telegraf.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.TYPE_CHAT
import com.example.telegraf.database.TYPE_GROUP
import com.example.telegraf.models.CommonModel
import com.example.telegraf.ui.screens.groups.GroupChatFragment
import com.example.telegraf.ui.screens.single_chat.SingleChatFragment
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.utilities.replaceFragment

class MainListAdapter(): RecyclerView.Adapter<MainListAdapter.ChatHolder>() {

    private val chatList = mutableListOf<CommonModel>();


    class ChatHolder(view: View): RecyclerView.ViewHolder(view){
        val chatPhoto = view.findViewById<ImageView>(R.id.chat_item_photo);
        val chatItemFullname = view.findViewById<TextView>(R.id.chat_item_fullname)
        val chatItemLastMessage = view.findViewById<TextView>(R.id.chat_item_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false);

        val holder = ChatHolder(view);
        holder.itemView.setOnClickListener{
            val chatModel = chatList[holder.bindingAdapterPosition]
            when(chatModel.type){
                TYPE_CHAT -> replaceFragment(SingleChatFragment(chatModel))
                TYPE_GROUP -> replaceFragment(GroupChatFragment(chatModel))
            }
        }
        return holder;
    }

    override fun getItemCount(): Int {
        return chatList.size;
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val model = chatList[position]
        holder.chatItemFullname.text = model.fullname
        holder.chatItemLastMessage.text = model.lastMessage
        holder.chatPhoto.downloadAndSetImage(model.photoUrl)
    }
    fun updateChatList(item: CommonModel){
        chatList.add(item);
        notifyItemInserted(chatList.size)
    }
}