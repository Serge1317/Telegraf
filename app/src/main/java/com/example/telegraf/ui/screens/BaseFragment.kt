package com.example.telegraf.ui.screens

import androidx.fragment.app.Fragment
import com.example.telegraf.utilities.APP_ACTIVITY


open class BaseFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.drawerDisable();
    }

}