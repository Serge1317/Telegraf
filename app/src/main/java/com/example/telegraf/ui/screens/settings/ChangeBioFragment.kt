package com.example.telegraf.ui.screens.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentChangeBioBinding
import com.example.telegraf.database.USER
import com.example.telegraf.database.changeBioToDatabase


class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    private var _binding: FragmentChangeBioBinding? = null;
    private val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeBioBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputBio.setText(USER.bio);
    }


    override fun change() {
        val newBio = binding.settingsInputBio.text.toString();
        changeBioToDatabase(newBio)
    }
}