package com.example.telegraf.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegraf.R
import com.example.telegraf.utilities.APP_ACTIVITY

class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegraf"
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.drawerEnable()
    }
}