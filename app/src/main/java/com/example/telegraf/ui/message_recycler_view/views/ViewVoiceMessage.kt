package com.example.telegraf.ui.message_recycler_view.views

data class ViewVoiceMessage(
    override val id: String,
    override val from: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String = ""
): MessageView {
    override fun getViewType(): Int {
        return MessageView.MESSAGE_VOICE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == this.id
    }
}
