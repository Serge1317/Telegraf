package com.example.telegraf.ui.screens.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegraf.R
import com.example.telegraf.models.CommonModel
import com.example.telegraf.ui.screens.single_chat.SingleChatFragment
import com.example.telegraf.utilities.downloadAndSetImage
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.showToast

class AddContactAdapter(): RecyclerView.Adapter<AddContactAdapter.AddContactHolder>() {

    private val addContactList = mutableListOf<CommonModel>();

    class AddContactHolder(view: View): RecyclerView.ViewHolder(view){
        val addContactPhoto = view.findViewById<ImageView>(R.id.add_contact_photo);
        val addContactFullname = view.findViewById<TextView>(R.id.add_contact_fullname)
        val addContactLastMessage = view.findViewById<TextView>(R.id.add_contact_last_message)
        val addContactChoice = view.findViewById<ImageView>(R.id.add_contact_choice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_contact_item, parent, false);

        val holder = AddContactHolder(view);
        holder.itemView.setOnClickListener{
            val contact = addContactList[holder.bindingAdapterPosition]
            if(contact.choice){
                holder.addContactChoice.visibility = View.INVISIBLE
                contact.choice = false;
                AddContactFragment.chosenContacts.remove(contact)
            }else{
                holder.addContactChoice.visibility = View.VISIBLE
                contact.choice = true;
                AddContactFragment.chosenContacts.add(contact)
            }
        }
        return holder;
    }

    override fun getItemCount(): Int {
        return addContactList.size;
    }

    override fun onBindViewHolder(holder: AddContactHolder, position: Int) {
        val model = addContactList[position]
        holder.addContactFullname.text = model.fullname
        holder.addContactLastMessage.text = model.lastMessage
        holder.addContactPhoto.downloadAndSetImage(model.photoUrl)
        holder.addContactChoice.visibility = View.INVISIBLE
    }
    fun updateAddContactList(item: CommonModel){
        addContactList.add(item);
        notifyItemInserted(addContactList.size)
    }
}