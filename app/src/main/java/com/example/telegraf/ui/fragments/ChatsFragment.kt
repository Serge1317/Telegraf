package com.example.telegraf.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.utilities.APP_ACTIVITY

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Чаты"
    }

    override fun onStart() {
        super.onStart()
    }
}