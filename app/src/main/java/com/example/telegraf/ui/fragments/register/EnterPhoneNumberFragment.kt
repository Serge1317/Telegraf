package com.example.telegraf.ui.fragments.register

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegraf.R
import com.example.telegraf.databinding.FragmentEnterPhoneNumberBinding
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.database.AUTH
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.restartActivity
import com.example.telegraf.utilities.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {
    private var _mBinding: FragmentEnterPhoneNumberBinding? = null
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
        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Добро пожаловать")
                        restartActivity()
                    } else
                        showToast(task.exception?.message.toString());
                }
            }

            @SuppressLint("LongLogTag")
            override fun onVerificationFailed(e: FirebaseException) {
                showToast(e.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))
            }
        }
        mBinding.registerBtnNext.setOnClickListener { onSend() }
    }

    private fun onSend() {
        if (mBinding.registerInputPhoneNumber.text.toString().isEmpty()) {
            showToast(getString(R.string.enter_your_phone_number))
        } else {
            authUser()
        }
    }

    @SuppressLint("LongLogTag")
    private fun authUser() {
        mPhoneNumber = mBinding.registerInputPhoneNumber.text.toString();
        mPhoneNumber = mPhoneNumber.replace(Regex("[\\s, -]"), "");
        val mPhoneNumberOptions = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(mPhoneNumber)
            .setActivity(APP_ACTIVITY)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(mPhoneNumberOptions)
    }

    override fun onDestroy() {
        super.onDestroy()
        _mBinding = null;
    }
}