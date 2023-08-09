package com.example.telegraf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.ActivityMainBinding
import com.example.telegraf.models.User
import com.example.telegraf.ui.fragments.ChatsFragment
import com.example.telegraf.ui.objects.AppDrawer
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AUTH
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.FOLDER_PROFILE_IMAGE
import com.example.telegraf.utilities.NODE_USERS
import com.example.telegraf.utilities.REF_DATABASE_ROOT
import com.example.telegraf.utilities.REF_STORAGE_ROOT
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.USER
import com.example.telegraf.utilities.initFirebase
import com.example.telegraf.utilities.replaceActivity
import com.example.telegraf.utilities.replaceFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private lateinit var toolbar: Toolbar;
    lateinit var mAppDrawer: AppDrawer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        APP_ACTIVITY = this;
        initFields();
        initFunc();
        initUser();

    }


    private fun initFields() {
        toolbar = binding.mainToolbar;
        mAppDrawer = AppDrawer(this, toolbar);
        initFirebase()
    }


    private fun initFunc() {
        if (AUTH.currentUser != null) {
            this.setSupportActionBar(toolbar); // сначала setSupport а потом create (иначе не работает бутерброд)
            mAppDrawer.create();
            replaceFragment(ChatsFragment(), false)
        } else {
            replaceActivity(RegisterActivity())
        }
    }

    private fun initUser() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
            .addListenerForSingleValueEvent(AppValueEventListener {
                USER = it.getValue(User::class.java) ?: User();
            })
    }
//    fun hideKeyboard(){
//        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0);
//    }


}