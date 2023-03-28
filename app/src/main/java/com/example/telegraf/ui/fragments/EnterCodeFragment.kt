package com.example.telegraf.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentEnterCodeBinding
import com.example.telegraf.utilities.AppTextWatcher
import com.example.telegraf.utilities.showToast


class EnterCodeFragment : Fragment(R.layout.fragment_enter_code) {

    private var _binding: FragmentEnterCodeBinding? = null;
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEnterCodeBinding.inflate(inflater, container, false);
        return binding.root;
    }
    override fun onStart() {
        super.onStart();
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            val code: String = binding.registerInputCode.text.toString();
            if (code.length == 6) {
                verifyCode()
            }
        })
    }
    private fun verifyCode(){
        showToast("код норм - (6 символов)")
    }
    override fun onDestroy(){
        super.onDestroy()
        _binding = null;
    }
}