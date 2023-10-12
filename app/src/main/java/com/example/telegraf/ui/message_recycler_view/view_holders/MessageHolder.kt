package com.example.telegraf.ui.message_recycler_view.view_holders

import com.example.telegraf.ui.message_recycler_view.views.MessageView

interface MessageHolder {
    fun drawMessage(messageView: MessageView)
    fun attach(messageView: MessageView);
    fun detach();
}