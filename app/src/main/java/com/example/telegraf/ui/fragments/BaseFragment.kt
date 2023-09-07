package com.example.telegraf.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegraf.MainActivity
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppState


open class BaseFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.drawerDisable();
    }
    override fun onStop(){
        super.onStop()
        APP_ACTIVITY.mAppDrawer.drawerEnable();
    }

}