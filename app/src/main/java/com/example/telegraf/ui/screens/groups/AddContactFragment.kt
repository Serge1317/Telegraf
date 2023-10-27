package com.example.telegraf.ui.screens.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.database.NODE_MAIN_LIST
import com.example.telegraf.database.NODE_MESSAGES
import com.example.telegraf.database.NODE_PHONE_CONTACTS
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.UID
import com.example.telegraf.database.getCommonModel
import com.example.telegraf.databinding.FragmentAddContactsBinding
import com.example.telegraf.databinding.FragmentChatsBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.ui.screens.BaseFragment
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.hideKeyboard
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.showToast

class AddContactFragment : BaseFragment(R.layout.fragment_add_contacts) {
    private var _binding: FragmentAddContactsBinding? = null;
    private val binding get() = _binding!!

    private lateinit var contactRecycler: RecyclerView;
    private lateinit var contactAdapter: AddContactAdapter;
    private val contactList = REF_DATABASE_ROOT.child(NODE_PHONE_CONTACTS).child(UID)
    private val refMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID)
    private val refUsers = REF_DATABASE_ROOT.child(NODE_USERS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactsBinding.inflate(layoutInflater, container, false)
        return binding.root;
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Добавить участника"
        hideKeyboard();
        initRecycler()
        binding.addContactBtnNext.setOnClickListener{
            if(chosenContacts.isEmpty()) showToast("Выберите участников")
            else replaceFragment(CreateGroupFragment(chosenContacts))
        }
    }

    override fun onStart() {
        super.onStart()
        chosenContacts.clear()
    }

    private fun initRecycler() {
        contactRecycler = binding.addContactRecyclerView;
        contactAdapter = AddContactAdapter();
        contactList.addListenerForSingleValueEvent(AppValueEventListener() { dataSnapshot ->
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
                                    newModel.lastMessage = "The list was cleared"
                                }else{
                                    newModel.lastMessage = tempList[0].text
                                }
                                if (newModel.fullname.isEmpty()) {
                                    newModel.fullname = newModel.phone
                                }
                                contactAdapter.updateAddContactList(newModel);
                            })
                    })
            }
        })
        contactRecycler.adapter = contactAdapter;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
    companion object{
        val chosenContacts = mutableListOf<CommonModel>();
    }
}