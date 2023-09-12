package com.example.telegraf.ui.fragments.single_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentSingleChatBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.models.User
import com.example.telegraf.ui.fragments.BaseFragment
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.NODE_MESSAGES
import com.example.telegraf.utilities.NODE_USERS
import com.example.telegraf.utilities.REF_DATABASE_ROOT
import com.example.telegraf.utilities.TYPE_TEXT
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.utilities.getCommonModel
import com.example.telegraf.utilities.getUserModel
import com.example.telegraf.utilities.sendMessage
import com.example.telegraf.utilities.showToast
import com.google.firebase.database.DatabaseReference
import io.reactivex.rxjava3.core.Single

class SingleChatFragment(private val model: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private var _binding: FragmentSingleChatBinding? = null
    private val binding get() = _binding!!;
    private lateinit var toolbarInfo: View;
    private lateinit var toolbarInfoListener: AppValueEventListener;
    private lateinit var receiveUser: User;
    private lateinit var refDatabase: DatabaseReference;
    private lateinit var chatRecycler: RecyclerView;
    private lateinit var chatAdapter: SingleChatAdapter;
    private lateinit var chatListener: AppValueEventListener;
    private lateinit var refMessages: DatabaseReference;
    private var chatList: List<CommonModel> = emptyList();


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSingleChatBinding.inflate(layoutInflater, container, false);
        return binding.root;
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecycler()
        binding.chatBtnSendMessage.setOnClickListener{
            val message: String = binding.chatInputMessage.text.toString();
            if(message.isEmpty()){
                showToast(getString(R.string.enter_message))
            }else{
                sendMessage(message, model.id, TYPE_TEXT){
                    binding.chatInputMessage.setText("");
                }
            }

        }
    }

    private fun initRecycler(){
        chatRecycler = binding.singleChatRecycler;
        chatAdapter = SingleChatAdapter();
        chatRecycler.adapter = chatAdapter;

        refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(UID)
            .child(model.id)
        chatListener = AppValueEventListener { dataSnapshot ->
            chatList = dataSnapshot.children.map{it.getCommonModel()}
            chatAdapter.setList(chatList)
            chatRecycler.smoothScrollToPosition(chatAdapter.itemCount);
        }
        refMessages.addValueEventListener(chatListener);
    }

    private fun initToolbar() {
        toolbarInfo = APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_info);
        toolbarInfo.visibility = View.VISIBLE;
        toolbarInfoListener = AppValueEventListener {
            receiveUser = it.getUserModel()
            initToolbarInfo()
        }
        refDatabase = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)
        refDatabase.addValueEventListener(toolbarInfoListener)
    }

    private fun initToolbarInfo() {
        val fullName = toolbarInfo.findViewById<TextView>(R.id.chat_contact_fullname)
        if (receiveUser.fullname.isEmpty())
            fullName.text = model.fullname;
        else
            fullName.text = receiveUser.fullname

        val status = toolbarInfo.findViewById<TextView>(R.id.chat_contact_status)
        status.text = receiveUser.state;

        val photo = toolbarInfo.findViewById<ImageView>(R.id.chat_toolbar_photo)
        photo.downloadAndSetImage(receiveUser.photoUrl)
    }

    override fun onPause() {
        super.onPause()
        toolbarInfo.visibility = View.GONE;
        refDatabase.removeEventListener(toolbarInfoListener)
        refMessages.removeEventListener(chatListener);
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

}