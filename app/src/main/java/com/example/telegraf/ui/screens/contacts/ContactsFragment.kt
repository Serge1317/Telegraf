package com.example.telegraf.ui.screens.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentContactsBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.ui.screens.single_chat.SingleChatFragment
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.database.NODE_PHONE_CONTACTS
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.UID
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.database.getCommonModel
import com.example.telegraf.ui.screens.BaseFragment
import com.example.telegraf.utilities.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private var _binding: FragmentContactsBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var recycler: RecyclerView;
    private lateinit var adapter: FirebaseRecyclerAdapter<CommonModel, ContactHolder>;
    private lateinit var refContacts: DatabaseReference;
    private lateinit var refUsers: DatabaseReference;
    private lateinit var refUsersListener: AppValueEventListener;
    private val mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>();

    override fun onStart() {
        super.onStart();
        APP_ACTIVITY.title = "Контакты"
        initRecyclerView();
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, bundle: Bundle?): View {
        _binding = FragmentContactsBinding.inflate(inflater, parent, false);
        return binding.root
    }

    private fun initRecyclerView() {
        recycler = binding.contactRecyclerView;
        refContacts = REF_DATABASE_ROOT.child(NODE_PHONE_CONTACTS).child(UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refContacts, CommonModel::class.java)
            .build();

        adapter = object : FirebaseRecyclerAdapter<CommonModel, ContactHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false);
                return ContactHolder(view)
            }
            // model is representing phone_contacts elements
            override fun onBindViewHolder(holder: ContactHolder, position: Int, contact: CommonModel) {
                refUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
                refUsersListener = AppValueEventListener {
                    val model = it.getCommonModel();
                    if(model.fullname.isEmpty()){
                        holder.name.text = contact.fullname;
                    }else{
                        holder.name.text = model.fullname;
                    }
                    holder.status.text = model.state;
                    holder.image.downloadAndSetImage(model.photoUrl);
                    holder.itemView.setOnClickListener{
                        replaceFragment(SingleChatFragment(contact));
                    }
                }
                refUsers.addValueEventListener(refUsersListener)
                mapListeners[refUsers] = refUsersListener;
            }
        }
        recycler.adapter = adapter;
        adapter.startListening();
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening();
        mapListeners.forEach{map ->
            map.key.removeEventListener(map.value);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

    class ContactHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.contact_fullname);
        val status: TextView = view.findViewById(R.id.contact_status)
        val image: CircleImageView = view.findViewById(R.id.contact_photo);
    }
}