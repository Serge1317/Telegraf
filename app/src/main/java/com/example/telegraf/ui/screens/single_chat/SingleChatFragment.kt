package com.example.telegraf.ui.screens.single_chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentSingleChatBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.models.User
import com.example.telegraf.ui.screens.BaseFragment
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.database.NODE_MESSAGES
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.TYPE_MESSAGE_FILE
import com.example.telegraf.database.TYPE_MESSAGE_IMAGE
import com.example.telegraf.database.TYPE_MESSAGE_TEXT
import com.example.telegraf.database.TYPE_MESSAGE_VOICE
import com.example.telegraf.database.UID
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.database.getCommonModel
import com.example.telegraf.database.getMessageKey
import com.example.telegraf.database.getUserModel
import com.example.telegraf.database.sendMessage
import com.example.telegraf.database.uploadFileToStorage
import com.example.telegraf.ui.message_recycler_view.views.AppViewFactory
import com.example.telegraf.utilities.AppChildEventListener
import com.example.telegraf.utilities.AppTextWatcher
import com.example.telegraf.utilities.AppVoiceRecorder
import com.example.telegraf.utilities.RECORD_AUDIO
import com.example.telegraf.utilities.checkPermission
import com.example.telegraf.utilities.getFilenameFromUri
import com.example.telegraf.utilities.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    private var isRecording: Boolean = false;
    private lateinit var voiceRecorder: AppVoiceRecorder;
    private lateinit var bottomSheet: BottomSheetBehavior<*>;

    private lateinit var imageLauncher: ActivityResultLauncher<Any?>;
    private lateinit var fileLauncher: ActivityResultLauncher<String>
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
                val messageKey = getMessageKey(contact.id)
                uploadFileToStorage(
                    uri,
                    messageKey,
                    contact.id,
                    TYPE_MESSAGE_IMAGE
                )
                doSmoothScroll = true;
            }
        }
        fileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){uri: Uri? ->
            uri?.let{
                val messageKey = getMessageKey(contact.id)
                val filename = getFilenameFromUri(uri)
                uploadFileToStorage(
                    uri,
                    messageKey,
                    contact.id,
                    TYPE_MESSAGE_FILE,
                    filename
                )
                doSmoothScroll = true;
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
        voiceRecorder = AppVoiceRecorder();
        bottomSheet = BottomSheetBehavior.from(binding.bottomSheetChoice.bottomSheetChoiceLayout)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    @SuppressLint("ClickableViewAccessibility")
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
            if(message.isEmpty() or isRecording){
                binding.chatBtnAttach.visibility = View.VISIBLE;
                binding.chatBtnVoice.visibility = View.VISIBLE;
                binding.chatBtnSendMessage.visibility = View.GONE
            }else{
                binding.chatBtnAttach.visibility = View.GONE;
                binding.chatBtnVoice.visibility = View.GONE
                binding.chatBtnSendMessage.visibility = View.VISIBLE;
            }
        })

        binding.chatBtnAttach.setOnClickListener{
            attach()
        }
       CoroutineScope(Dispatchers.IO).launch{
            binding.chatBtnVoice.setOnTouchListener { view, event: MotionEvent ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        isRecording = true;
                        val messageKey = getMessageKey(contact.id);
                        voiceRecorder.startRecord(messageKey);
                        binding.chatInputMessage.setText("Запись...")
                        binding.chatBtnVoice.setColorFilter(R.color.primary);
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        isRecording = false;
                        voiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                contact.id,
                                TYPE_MESSAGE_VOICE
                            )
                            doSmoothScroll = true;
                        }
                        binding.chatInputMessage.setText("")
                        binding.chatBtnVoice.colorFilter = null;
                    }
                }
                true;
            }
        }
    }

    private fun attach(){
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

        binding.bottomSheetChoice.btnAttachFile.setOnClickListener{
            fileLauncher.launch("*/*")
        }
        binding.bottomSheetChoice.btnAttachImage.setOnClickListener{
            imageLauncher.launch(null);
        }
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
                chatAdapter.addItemToBottom(AppViewFactory.getView(message)) {
                    chatRecycler.smoothScrollToPosition(chatAdapter.itemCount);
                }
            } else {
                chatAdapter.addItemToTop(AppViewFactory.getView(message)) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        voiceRecorder.releaseRecorder();
        chatAdapter.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}