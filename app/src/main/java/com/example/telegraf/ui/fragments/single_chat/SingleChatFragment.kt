package com.example.telegraf.ui.fragments.single_chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegraf.R
import com.example.telegraf.database.FOLDER_MESSAGE_IMAGE
import com.example.telegraf.databinding.FragmentSingleChatBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.models.User
import com.example.telegraf.ui.fragments.BaseFragment
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.database.NODE_MESSAGES
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.REF_STORAGE_ROOT
import com.example.telegraf.database.TYPE_MESSAGE_TEXT
import com.example.telegraf.database.UID
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.database.getCommonModel
import com.example.telegraf.database.getUrlFromStorage
import com.example.telegraf.database.getUserModel
import com.example.telegraf.database.putImageToStorage
import com.example.telegraf.database.putUrlToDatabase
import com.example.telegraf.database.sendMessage
import com.example.telegraf.database.sendMessageAsImage
import com.example.telegraf.utilities.AppChildEventListener
import com.example.telegraf.utilities.AppTextWatcher
import com.example.telegraf.utilities.showToast
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private var _binding: FragmentSingleChatBinding? = null
    private val binding get() = _binding!!;
    private lateinit var toolbarInfo: View;
    private lateinit var toolbarInfoListener: AppValueEventListener;
    private lateinit var receiveUser: User;
    private lateinit var refDatabase: DatabaseReference;
    private lateinit var chatRecycler: RecyclerView;

    private lateinit var chatAdapter: SingleChatAdapter;
    private lateinit var chatListener: AppChildEventListener;
    private lateinit var refMessages: DatabaseReference;

    private lateinit var chatLayoutManager: LinearLayoutManager;
    private var countMessages: Int = 10; // how many messages will be load at once
    private var isScrolling: Boolean = false;
    private var doSmoothScroll: Boolean = true;
    private lateinit var swipeLayout: SwipeRefreshLayout;

    private lateinit var imageLauncher: ActivityResultLauncher<Any?>;
    private val imageContract = object: ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent{
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .setRequestedSize(250, 250)
                .getIntent(APP_ACTIVITY)
        }
        override fun parseResult(resultCode: Int, intent: Intent?): Uri?{
            return CropImage.getActivityResult(intent)?.uri;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageLauncher = registerForActivityResult(imageContract){uri: Uri? ->
            uri?.let{
                val messageKey = REF_DATABASE_ROOT
                    .child(NODE_MESSAGES)
                    .child(UID)
                    .child(contact.id)
                    .push().key.toString();

                val path = REF_STORAGE_ROOT
                    .child(FOLDER_MESSAGE_IMAGE)
                    .child(messageKey);
                putImageToStorage(uri, path){
                    getUrlFromStorage(path){url: String ->
                        sendMessageAsImage(contact.id, url, messageKey)
                        doSmoothScroll = true;
                    }
                }
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSingleChatBinding.inflate(layoutInflater, container, false);
        return binding.root;
    }

    private fun initFields() {
        swipeLayout = binding.chatSwipeRefresh;
        chatLayoutManager = LinearLayoutManager(this.context);

    }

    override fun onResume() {
        super.onResume()
        initFields();
        initToolbar()
        initRecycler()
        binding.chatBtnSendMessage.setOnClickListener {
            doSmoothScroll = true;
            val message: String = binding.chatInputMessage.text.toString();
            if (message.isEmpty()) {
                showToast(getString(R.string.enter_message))
            } else {
                sendMessage(message, contact.id, TYPE_MESSAGE_TEXT) {
                    binding.chatInputMessage.setText("");
                }
            }
        }
        binding.chatInputMessage.addTextChangedListener(AppTextWatcher(){
           val message = binding.chatInputMessage.text.toString();
            if(message.isEmpty()){
                binding.chatBtnAttach.visibility = View.VISIBLE;
                binding.chatBtnSendMessage.visibility = View.GONE
            }else{
                binding.chatBtnAttach.visibility = View.GONE;
                binding.chatBtnSendMessage.visibility = View.VISIBLE;
            }
        })

        binding.chatBtnAttach.setOnClickListener{
            attachFile()
        }

    }
    private fun attachFile(){
        imageLauncher.launch(null);
    }

    private fun initRecycler() {
        chatRecycler = binding.singleChatRecycler;
        chatAdapter = SingleChatAdapter();
        chatRecycler.layoutManager = chatLayoutManager;
        chatRecycler.adapter = chatAdapter;
        chatRecycler.setHasFixedSize(true);
        chatRecycler.isNestedScrollingEnabled = false;

        refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(UID)
            .child(contact.id)

        chatListener = AppChildEventListener {
            val message = it.getCommonModel();
            if (doSmoothScroll) {
                chatAdapter.addItemToBottom(message) {
                    chatRecycler.smoothScrollToPosition(chatAdapter.itemCount);
                }
            } else {
                chatAdapter.addItemToTop(message) {
                    swipeLayout.isRefreshing = false;
                }
            }
        }
        refMessages.limitToLast(countMessages)
            .addChildEventListener(chatListener);

        chatRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // scrolling up (the list moves down)
                if (isScrolling
                    && dy < 0
                    && chatLayoutManager.findFirstVisibleItemPosition() <= 3
                ) {
                    loadMoreMessages();
                }
            }
        })
        swipeLayout.setOnRefreshListener {
            loadMoreMessages();
        }
    }

    private fun loadMoreMessages() {
        doSmoothScroll = false;
        isScrolling = false;
        countMessages += 10;
        refMessages.removeEventListener(chatListener)
        refMessages.limitToLast(countMessages)
            .addChildEventListener(chatListener)
    }

    private fun initToolbar() {
        toolbarInfo = APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_info);
        toolbarInfo.visibility = View.VISIBLE;
        toolbarInfoListener = AppValueEventListener {
            receiveUser = it.getUserModel()
            initToolbarInfo()
        }
        refDatabase = REF_DATABASE_ROOT
            .child(NODE_USERS)
            .child(contact.id)
        refDatabase.addValueEventListener(toolbarInfoListener)
    }

    private fun initToolbarInfo() {
        val fullName = toolbarInfo.findViewById<TextView>(R.id.chat_contact_fullname)
        if (receiveUser.fullname.isEmpty())
            fullName.text = contact.fullname;
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