package com.example.telegraf.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentEnterPhoneNumberBinding


class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {
    private  var _mBinding: FragmentEnterPhoneNumberBinding? =  null
    private val mBinding get() = _mBinding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _mBinding = FragmentEnterPhoneNumberBinding.inflate(inflater, container, false)
        val view = mBinding.root;

        return view;
    }
    override fun onStart() {
        super.onStart()
        // раньше использовалься Kotlin synthetic: register_btn_next.setOnClickListener{}
        mBinding.registerBtnNext.setOnClickListener{ onSend()}
    }

    private fun onSend() {
        if(mBinding.registerInputPhoneNumber.text.toString().isEmpty()){
            Toast.makeText(context, getString(R.string.enter_your_phone_number), Toast.LENGTH_SHORT).show();
        }else{
            parentFragmentManager.beginTransaction()
                .replace(R.id.registerDataContainer, EnterCodeFragment())
                .addToBackStack(null)
                .commit();
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        _mBinding = null;
    }
}