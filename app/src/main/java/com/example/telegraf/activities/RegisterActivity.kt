package com.example.telegraf.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.telegraf.R

import com.example.telegraf.databinding.ActivityRegisterBinding
import com.example.telegraf.ui.fragments.EnterPhoneNumberFragment
import com.example.telegraf.utilities.initFirebase
import com.example.telegraf.utilities.replaceFragment

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolbar: Toolbar;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater);
        setContentView(mBinding.root);
        initFirebase()

    }
    override fun onStart(){
        super.onStart()
        mToolbar = mBinding.registerToolbar;
        setSupportActionBar(mToolbar)
        this.title = this.getString(R.string.register_title_your_phone)
        this.replaceFragment(EnterPhoneNumberFragment(), false)
    }

}