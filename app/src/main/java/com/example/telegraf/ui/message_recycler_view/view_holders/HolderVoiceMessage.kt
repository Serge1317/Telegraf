package com.example.telegraf.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.UID
import com.example.telegraf.ui.message_recycler_view.views.MessageView
import com.example.telegraf.utilities.AppVoicePlayer
import com.example.telegraf.utilities.asTime

class HolderVoiceMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val voicePlayer: AppVoicePlayer = AppVoicePlayer()

    //user message
    private val blockUserVoice = view.findViewById<ConstraintLayout>(R.id.block_user_voice);
    private val chatUserVoicePlay = view.findViewById<ImageView>(R.id.chat_user_voice_play);
    private val chatUserVoiceStop = view.findViewById<ImageView>(R.id.chat_user_voice_stop);
    private val chatUserMessageTimeVoice = view.findViewById<TextView>(R.id.chat_user_time_voice);

    //received message
    private val blockReceivedVoice = view.findViewById<ConstraintLayout>(R.id.block_receive_voice);
    private val chatReceivedVoicePlay = view.findViewById<ImageView>(R.id.chat_receive_voice_play);
    private val chatReceivedVoiceStop = view.findViewById<ImageView>(R.id.chat_receive_voice_stop);
    private val chatReceivedMessageTimeVoice =
        view.findViewById<TextView>(R.id.chat_receive_time_voice);

    override fun drawMessage(messageView: MessageView) {

        if (messageView.from == UID) {
            blockUserVoice.visibility = View.VISIBLE;
            blockReceivedVoice.visibility = View.GONE;
            chatUserMessageTimeVoice.text = messageView.timeStamp.asTime()
        } else {
            blockUserVoice.visibility = View.GONE;
            blockReceivedVoice.visibility = View.VISIBLE
            chatReceivedMessageTimeVoice.text = messageView.timeStamp.asTime()
        }
    }

    override fun attach(messageView: MessageView) {
        voicePlayer.init();
        if (messageView.from == UID) {
            chatUserVoicePlay.setOnClickListener {
                userPlayBtnVisible(false)
                chatUserVoiceStop.setOnClickListener{
                    stop{
                        chatUserVoiceStop.setOnClickListener(null)
                        userPlayBtnVisible(true)
                    }
                }
                play(messageView) {
                    userPlayBtnVisible(true)
                }
            }
        } else {
            chatReceivedVoicePlay.setOnClickListener {
                receivedPlayBtnVisible(false)
                chatReceivedVoiceStop.setOnClickListener{
                    stop{
                        chatReceivedVoiceStop.setOnClickListener(null)
                        receivedPlayBtnVisible(true)
                    }
                }
                play(messageView){
                    receivedPlayBtnVisible(true)
                }
            }
        }
    }
    // otherwise stop button is visible
    private fun userPlayBtnVisible(isVisible: Boolean){
        if(isVisible){
            chatUserVoicePlay.visibility = View.VISIBLE
            chatUserVoiceStop.visibility = View.GONE
        }else{
            chatUserVoicePlay.visibility = View.GONE
            chatUserVoiceStop.visibility = View.VISIBLE
        }
    }
    // otherwise stop button is visible
    private fun receivedPlayBtnVisible(isVisible: Boolean){
        if(isVisible){
            chatReceivedVoicePlay.visibility = View.VISIBLE
            chatReceivedVoiceStop.visibility = View.GONE;
        }else{
            chatReceivedVoicePlay.visibility = View.GONE
            chatReceivedVoiceStop.visibility = View.VISIBLE
        }
    }

    override fun detach() {
        chatUserVoicePlay.setOnClickListener(null);
        chatReceivedVoicePlay.setOnClickListener(null)
        voicePlayer.release()
    }

    private fun play(messageView: MessageView, function: () -> Unit){
        voicePlayer.play(messageView.id, messageView.fileUrl){
            function();
        }
    }
    private fun stop(function: () -> Unit){
        voicePlayer.stop{
            function()
        }
    }
}