package com.example.telegraf.ui.screens.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.telegraf.R
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.addMenuProvider
import com.example.telegraf.utilities.hideKeyboard

open class BaseChangeFragment(layout: Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.drawerDisable();
    }

    override fun onStop(){
        super.onStop()
        hideKeyboard();
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
