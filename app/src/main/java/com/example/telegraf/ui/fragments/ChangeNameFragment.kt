package com.example.telegraf.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentChangeNameBinding
import com.example.telegraf.utilities.CHILD_FULLNAME
import com.example.telegraf.utilities.NODE_USERS
import com.example.telegraf.utilities.REF_DATABASE_ROOT
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.USER
import com.example.telegraf.utilities.showToast


class ChangeNameFragment : BaseFragment(R.layout.fragment_change_name) {

    private var _binding: FragmentChangeNameBinding? = null;
    private val binding get() = _binding!!

    private lateinit var userName: String;
    private lateinit var userSurname: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        addMenu();
        showCurrentUsername();
    }

    private fun showCurrentUsername() {
        val fullnameList: List<String> = USER.fullname.split(" ");
        binding.settingsInputName.setText(fullnameList[0]);
        if (fullnameList.size > 1)
            binding.settingsInputSurname.setText(fullnameList[1])
    }

    private fun addMenu() {
        val menuHost: FragmentActivity = requireActivity();
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                    inflater.inflate(R.menu.settings_menu_confirm, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.settings_confirm_change -> changeName();
                    }
                    return true;
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED
        )
    }

    private fun changeName() {
        userName = binding.settingsInputName.text.toString();
        userSurname = binding.settingsInputSurname.text.toString();
        if (userName.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullname = "$userName $userSurname"
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME)
                .setValue(fullname)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        USER.fullname = fullname;
                        showToast(getString(R.string.toast_data_update))
                        parentFragmentManager.popBackStack();
                    }
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false);
        val view = binding.root;
        return view;
    }

    override fun onDestroy() {
        super.onDestroy();
        _binding = null;
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChangeNameFragment()
    }

}