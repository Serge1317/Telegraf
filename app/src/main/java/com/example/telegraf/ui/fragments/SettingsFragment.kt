package com.example.telegraf.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import com.example.telegraf.R

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        this.setHasOptionsMenu(true)
    }

    override fun  onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        this.activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu);
    }

}