package com.example.telegraf.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.FragmentEnterPhoneNumberBinding
import com.example.telegraf.utilities.AUTH
import com.example.telegraf.utilities.replaceActivity
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

private const val TAG = "EnterPhoneNumberFragment"

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {
    private  var _mBinding: FragmentEnterPhoneNumberBinding? =  null
    private val mBinding get() = _mBinding!!
    private lateinit var mPhoneNumber: String;
    private lateinit var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks;

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
        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential : PhoneAuthCredential){
                AUTH.signInWithCredential(credential).addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        showToast("Добро пожаловать")
                        (activity as RegisterActivity).replaceActivity(MainActivity())
                    }else
                        showToast(task.exception?.message.toString());
                }
            }
            @SuppressLint("LongLogTag")
            override fun onVerificationFailed(e: FirebaseException){
                showToast(e.message.toString())
                Log.d(TAG, "onVerificationFailed: $e")
            }
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken){
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))
            }
        }
        mBinding.registerBtnNext.setOnClickListener{ onSend()}
    }

    private fun onSend() {
        if(mBinding.registerInputPhoneNumber.text.toString().isEmpty()){
            showToast(getString(R.string.enter_your_phone_number))
        }else{
            authUser()
        }
    }
    @SuppressLint("LongLogTag")
    private fun authUser(){
        mPhoneNumber = mBinding.registerInputPhoneNumber.text.toString();
        Log.d(TAG, "mPhoneNumber: $mPhoneNumber")
        val mPhoneNumberOptions = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(mPhoneNumber)
            .setActivity(activity as RegisterActivity)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(mPhoneNumberOptions)
    }

    override fun onDestroy(){
        super.onDestroy()
        _mBinding = null;
    }
}