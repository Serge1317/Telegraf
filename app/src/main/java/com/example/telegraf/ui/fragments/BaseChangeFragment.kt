package com.example.telegraf.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.utilities.addMenuProvider

open class BaseChangeFragment(layout: Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mAppDrawer.drawerDisable();
    }

    override fun onStop() {
        super.onStop()

    }
    override fun onViewCreated(view: View, bundle: Bundle?) {
        this.addMenuProvider(R.menu.settings_menu_confirm) {
             when (it) {
                R.id.settings_confirm_change -> {
                    change()
                    true;
                }
                 else -> false;
            }
        }
    }
    open fun change() {

    }

}
