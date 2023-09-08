package com.example.telegraf.ui.fragments

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentSingleChatBinding
import com.example.telegraf.models.CommonModel
import com.example.telegraf.utilities.APP_ACTIVITY

class SingleChatFragment (private val model: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {
    private var _binding: FragmentSingleChatBinding? = null
    private val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSingleChatBinding.inflate(layoutInflater, container, false);
        return binding.root;
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_info).visibility = View.VISIBLE;
    }

    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.toolbar.findViewById<View>(R.id.toolbar_info).visibility = View.GONE;
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

}