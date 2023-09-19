package com.example.telegraf.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegraf.R
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.hideKeyboard

class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegraf"
        hideKeyboard();
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.drawerEnable()
    }
}