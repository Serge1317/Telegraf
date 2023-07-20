package com.example.telegraf.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.utilities.AUTH
import com.example.telegraf.utilities.replaceActivity

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, bundle: Bundle?) {
        val menuHost = requireActivity();
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                    inflater.inflate(R.menu.settings_action_menu, menu);
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.settings_menu_exit -> {
                            AUTH.signOut();
                            (activity as MainActivity).replaceActivity(RegisterActivity())
                        }
                    }
                    return true;
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED
        )
    }
}