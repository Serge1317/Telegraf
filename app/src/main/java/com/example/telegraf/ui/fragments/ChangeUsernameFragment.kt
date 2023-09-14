package com.example.telegraf.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentChangeUsernameBinding
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.database.CHILD_USERNAME
import com.example.telegraf.database.NODE_USERNAMES
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.UID
import com.example.telegraf.database.USER
import com.example.telegraf.database.changeUsername
import com.example.telegraf.utilities.showToast
import java.util.Locale


class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {
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
    }


    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle);
        binding.settingsInputUsername.setText(USER.username)
    }

    override fun change() {
        mUserName = binding.settingsInputUsername.text.toString().lowercase(Locale.ROOT)
        if (mUserName.isEmpty()) {
            showToast(this.getString(R.string.settings_toast_name_is_empty))
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mUserName)) {
                        showToast(getString(R.string.toast_user_exist))
                    } else {
                        changeUsername(mUserName);
                    }
                })
        }
    }
}