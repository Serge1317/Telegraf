package com.example.telegraf.ui.screens.chat_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.NODE_MAIN_LIST
import com.example.telegraf.database.NODE_MESSAGES
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.UID
import com.example.telegraf.database.getCommonModel
import com.example.telegraf.databinding.FragmentChatsBinding
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.hideKeyboard
import com.example.telegraf.utilities.showToast

class ChatsFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null;
    private val binding get() = _binding!!

    private lateinit var mainRecycler: RecyclerView;
    private lateinit var mainAdapter: ChatAdapter;
    private val mainChatList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID)
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(layoutInflater, container, false)
        return binding.root;
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegraf"
        hideKeyboard();
        initRecycler()
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.drawerEnable()
    }

    private fun initRecycler() {
        mainRecycler = binding.chatRecycler;
        mainAdapter = ChatAdapter();
        mainChatList.addListenerForSingleValueEvent(AppValueEventListener() { dataSnapshot ->
            // modelList contains models with only id and type parameters
            val modelList = dataSnapshot.children.map { it.getCommonModel() }
            modelList.forEach { model ->
                refUsers.child(model.id)
                    .addListenerForSingleValueEvent(AppValueEventListener() { dataSnapshot2 ->
                        // newModel contains full information about our contact
                        val newModel = dataSnapshot2.getCommonModel();

                        refMessages.child(model.id).limitToLast(1)
                            .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot3 ->

                                val tempList = dataSnapshot3.children.map { it.getCommonModel() }
                                if(tempList.isEmpty()){
                                    newModel.lastMessage = "The chat was cleared"
                                }else{
                                    newModel.lastMessage = tempList[0].text
                                }
                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                mainAdapter.updateChatList(newModel);
                            })
                    })
            }
        })
        mainRecycler.adapter = mainAdapter;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}