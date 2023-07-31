package com.example.telegraf.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentChangeNameBinding
import com.example.telegraf.databinding.FragmentChangeUsernameBinding
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.CHILD_USERNAME
import com.example.telegraf.utilities.NODE_USERNAMES
import com.example.telegraf.utilities.NODE_USERS
import com.example.telegraf.utilities.REF_DATABASE_ROOT
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.USER
import com.example.telegraf.utilities.showToast
import java.util.Locale


class ChangeUsernameFragment : BaseFragment(R.layout.fragment_change_username), MenuProvider {
    private var _binding: FragmentChangeUsernameBinding? = null;
    private val binding get() = _binding!!;
    private var mUserName: String = "";
    private lateinit var menuHost: FragmentActivity;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        _binding = FragmentChangeUsernameBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
        //menuHost.removeMenuProvider(this);
    }

    /**
     * вынести код создания меню в Funs.kt как функцию разширения
     */
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.settings_menu_confirm, menu);
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.settings_confirm_change -> {
                change()
                true;
            }
            else -> false;
        }
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        binding.settingsInputUsername.setText(USER.username)
        menuHost = this.requireActivity();
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED);
    }

    private fun change() {
        mUserName = binding.settingsInputUsername.text.toString().lowercase(Locale.ROOT)
        if (mUserName.isEmpty()) {
            showToast(this.getString(R.string.settings_toast_name_is_empty))
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mUserName)) {
                        showToast(getString(R.string.toast_user_exist))
                    } else {
                        changeUsername();
                    }
                })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mUserName).setValue(UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername()
                }
            }
    }

    private fun updateCurrentUsername() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).setValue(mUserName)
            .addOnCompleteListener() {
                if (it.isSuccessful) {
                    showToast(resources.getString(R.string.toast_data_update))
                    deleteOldUsername()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deleteOldUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(resources.getString(R.string.toast_data_update))
                    USER.username = mUserName;
                    parentFragmentManager.popBackStack();
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }
}