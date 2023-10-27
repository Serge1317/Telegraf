package com.example.telegraf.ui.screens.main_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.database.NODE_GROUPS
import com.example.telegraf.database.NODE_MAIN_LIST
import com.example.telegraf.database.NODE_MESSAGES
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.TYPE_CHAT
import com.example.telegraf.database.TYPE_GROUP
import com.example.telegraf.database.UID
import com.example.telegraf.database.getCommonModel
import com.example.telegraf.databinding.FragmentChatsBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.hideKeyboard

class MainListFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null;
    private val binding get() = _binding!!

    private lateinit var mainRecycler: RecyclerView;
    private lateinit var mainAdapter: MainListAdapter;
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
        APP_ACTIVITY.title = "Чаты"
        hideKeyboard();
        initRecycler()
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.drawerEnable()
    }

    private fun initRecycler() {
        mainRecycler = binding.chatRecycler;
        mainAdapter = MainListAdapter();
        mainChatList.addListenerForSingleValueEvent(AppValueEventListener() { dataSnapshot ->
            // modelList contains models with only id and type parameters
            val modelList = dataSnapshot.children.map { it.getCommonModel() }
            modelList.forEach { model ->
                when(model.type){
                    TYPE_CHAT -> showChat(model)
                    TYPE_GROUP -> showGroup(model)
                }
            }
        })
        mainRecycler.adapter = mainAdapter;
    }

    private fun showGroup(model: CommonModel) {
        REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener() { dataSnapshot2 ->
                // newModel contains full information about our contact
                val newModel = dataSnapshot2.getCommonModel();

               REF_DATABASE_ROOT.child(NODE_GROUPS).child(model.id).child(NODE_MESSAGES).limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot3 ->

                        val tempList = dataSnapshot3.children.map { it.getCommonModel() }
                        if(tempList.isEmpty()){
                            newModel.lastMessage = "The chat was cleared"
                        }else{
                            newModel.lastMessage = tempList[0].text
                        }
                        newModel.type = TYPE_GROUP
                        mainAdapter.updateChatList(newModel);
                    })
            })
    }

    private fun showChat(model: CommonModel) {
        refUsers.child(model.id)
            .addListenerForSingleValueEvent(AppValueEventListener() { dataSnapshot2 ->
                // newModel contains full information about our contact
                val newModel = dataSnapshot2.getCommonModel();

                refMessages.child(model.id).limitToLast(1)
                    .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot3 ->

                        val tempList = dataSnapshot3.children.map { it.getCommonModel() }
                        if(tempList.isEmpty()){
                            newModel.lastMessage = "The chat is empty"
                        }else{
                            newModel.lastMessage = tempList[0].text
                        }
                        if (newModel.fullname.isEmpty()) {
                            newModel.fullname = newModel.phone
                        }
                        newModel.type = TYPE_CHAT
                        mainAdapter.updateChatList(newModel);
                    })
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}