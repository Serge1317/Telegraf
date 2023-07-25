package com.example.telegraf.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.FragmentSettingsBinding
import com.example.telegraf.utilities.AUTH
import com.example.telegraf.utilities.USER
import com.example.telegraf.utilities.replaceActivity
import com.example.telegraf.utilities.replaceFragment

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null;
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View{
        _binding = FragmentSettingsBinding.inflate(inflater, container, false);
        val view = binding.root;
        return view;
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        addMenu();
        initFields();
    }
    private fun initFields(){
        binding.settingsFullname.text = USER.fullname
        binding.settingsPhoneNumber.text = USER.phone;
        binding.settingsBio.text = USER.bio;
        binding.settingsStatus.text = USER.status;
        binding.settingsUsername.text = USER.username
    }

    private fun addMenu(){

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
                        R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment());
                    }
                    return true;
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED
        )
    }
    override fun onDestroy(){
        super.onDestroy()
        _binding = null;
    }
}