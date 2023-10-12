package com.example.telegraf.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.ui.message_recycler_view.view_holders.AppHolderFactory
import com.example.telegraf.ui.message_recycler_view.view_holders.MessageHolder
import com.example.telegraf.ui.message_recycler_view.views.MessageView

typealias Holder = RecyclerView.ViewHolder

class SingleChatAdapter() : RecyclerView.Adapter<Holder>() {

    private var cacheList = mutableListOf<MessageView>();
    private val holderList = mutableListOf<MessageHolder>();

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return AppHolderFactory.getViewHolder(parent, viewType);
    }

    override fun getItemCount(): Int {
        return cacheList.size;
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        (holder as MessageHolder).drawMessage(cacheList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return cacheList[position].getViewType()
    }

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit){
        if ( ! cacheList.contains(item)) {
            cacheList.add(item);
            this.notifyItemInserted(cacheList.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit){
        if ( ! cacheList.contains(item)) {
            cacheList.add(item);
            cacheList.sortBy { it.timeStamp.toString() }
            this.notifyItemInserted(0)
        }
        onSuccess();
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        (holder as MessageHolder)
            .attach(cacheList[holder.bindingAdapterPosition])
        holderList.add((holder as MessageHolder))
    }

    override fun onViewDetachedFromWindow(holder: Holder) {
        super.onViewDetachedFromWindow(holder)
        (holder as MessageHolder).detach()
        holderList.remove(holder as MessageHolder)
    }
    fun destroy(){
        holderList.forEach{
            it.detach();
        }
    }
}


