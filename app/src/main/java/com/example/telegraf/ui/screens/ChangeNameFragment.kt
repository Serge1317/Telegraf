package com.example.telegraf.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentChangeNameBinding
import com.example.telegraf.database.USER
import com.example.telegraf.database.changeNameToDatabase
import com.example.telegraf.utilities.showToast


class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private var _binding: FragmentChangeNameBinding? = null;
    private val binding get() = _binding!!

    private lateinit var userName: String;
    private lateinit var userSurname: String;

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFullname();
    }

    private fun initFullname() {
        val fullnameList: List<String> = USER.fullname.split(" ");
        binding.settingsInputName.setText(fullnameList[0]);
        if (fullnameList.size > 1)
            binding.settingsInputSurname.setText(fullnameList[1])
    }

    override fun change() {
        changeName();
    }
    private fun changeName() {
        userName = binding.settingsInputName.text.toString();
        userSurname = binding.settingsInputSurname.text.toString();
        if (userName.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullname = "$userName $userSurname"
            changeNameToDatabase(fullname)
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