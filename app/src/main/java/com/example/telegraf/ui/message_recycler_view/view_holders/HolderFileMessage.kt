package com.example.telegraf.ui.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.UID
import com.example.telegraf.database.getFileFromStorage
import com.example.telegraf.ui.message_recycler_view.views.MessageView
import com.example.telegraf.utilities.WRITE_STORAGE
import com.example.telegraf.utilities.asTime
import com.example.telegraf.utilities.checkPermission
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    //user message
    private val blockUserFile = view.findViewById<ConstraintLayout>(R.id.block_user_file);
    private val chatUserFile = view.findViewById<ImageView>(R.id.chat_user_file);
    private val chatUserFileProgress = view.findViewById<ProgressBar>(R.id.chat_user_file_progress);
    private val chatUserFilename = view.findViewById<TextView>(R.id.chat_user_filename)
    private val chatUserMessageTimeFile = view.findViewById<TextView>(R.id.chat_user_time_file);

    //received message
    private val blockReceivedFile = view.findViewById<ConstraintLayout>(R.id.block_receive_file);
    private val chatReceivedFile = view.findViewById<ImageView>(R.id.chat_receive_file);
    private val chatReceivedProgress = view.findViewById<ProgressBar>(R.id.chat_receive_file_progress);
    private val chatReceivedFilename = view.findViewById<TextView>(R.id.chat_received_filename);
    private val chatReceivedMessageTimeFile =
        view.findViewById<TextView>(R.id.chat_receive_time_file);

    override fun drawMessage(messageView: MessageView) {

        if (messageView.from == UID) {
            blockUserFile.visibility = View.VISIBLE;
            blockReceivedFile.visibility = View.GONE;
            chatUserFilename.text = messageView.text
            chatUserMessageTimeFile.text = messageView.timeStamp.asTime()
        } else {
            blockUserFile.visibility = View.GONE;
            blockReceivedFile.visibility = View.VISIBLE
            chatReceivedFilename.text = messageView.text
            chatReceivedMessageTimeFile.text = messageView.timeStamp.asTime()
        }
    }

    override fun attach(messageView: MessageView) {

        if (messageView.from == UID) {
            chatUserFile.setOnClickListener{
                clickOnBtnFile(messageView);
            }
        } else {
            chatReceivedFile.setOnClickListener{
                clickOnBtnFile(messageView);
            }
        }
    }

    private fun clickOnBtnFile(messageView: MessageView){
        if(messageView.from == UID){
            chatUserFile.visibility = View.INVISIBLE
            chatUserFileProgress.visibility = View.VISIBLE
        }else{
            chatReceivedFile.visibility = View.INVISIBLE
            chatReceivedProgress.visibility = View.VISIBLE
        }
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            messageView.text
        );
        try{
            if(checkPermission(WRITE_STORAGE)){
                file.createNewFile()
                getFileFromStorage(file, messageView.fileUrl){
                    if(messageView.from == UID){
                        chatUserFile.visibility = View.VISIBLE
                        chatUserFileProgress.visibility = View.INVISIBLE
                    }else{
                        chatReceivedFile.visibility = View.VISIBLE
                        chatReceivedProgress.visibility = View.INVISIBLE
                    }
                }
            }
        }catch(e: Exception){
            e.message.toString()
        }
    }

    override fun detach() {
        chatUserFile.setOnClickListener(null);
        chatReceivedFile.setOnClickListener(null);
    }
}