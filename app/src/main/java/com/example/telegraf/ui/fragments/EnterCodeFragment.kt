package com.example.telegraf.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.FragmentEnterCodeBinding
import com.example.telegraf.utilities.*
import com.google.firebase.auth.EmailAuthProvider.getCredential
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.PhoneAuthProvider


class EnterCodeFragment(val mPhoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

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
        (activity as RegisterActivity).title = mPhoneNumber;
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            val code: String = binding.registerInputCode.text.toString();
            if (code.length == 6) {
                enterCode()
            }
        })
    }

    private fun enterCode() {
        val code: String = binding.registerInputCode.text.toString();
        val credential = PhoneAuthProvider.getCredential(id, code);
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString();
                val dateMap = mutableMapOf<String, Any>();
                dateMap[CHILD_ID] = uid;
                dateMap[CHILD_PHONE] = mPhoneNumber
                //dateMap[CHILD_USERNAME] = uid;

                REF_DATABASE_ROOT.child(NODE_PHONES).child(mPhoneNumber).setValue(uid)
                    .addOnFailureListener {
                        showToast(it.message.toString())
                    }.addOnSuccessListener {
                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                        .addOnSuccessListener {
                            showToast("Добро пожаловать")
                            (activity as RegisterActivity).replaceActivity(MainActivity())
                        }.addOnFailureListener{
                            showToast(it.message.toString())
                        }
                }

            } else
                showToast(task.exception?.message.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }
}