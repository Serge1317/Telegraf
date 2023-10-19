package com.example.telegraf

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

import com.example.telegraf.databinding.ActivityMainBinding
import com.example.telegraf.ui.screens.chat_list.ChatsFragment
import com.example.telegraf.ui.screens.register.EnterPhoneNumberFragment
import com.example.telegraf.ui.objects.AppDrawer
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.database.AUTH
import com.example.telegraf.utilities.AppState
import com.example.telegraf.utilities.READ_CONTACTS
import com.example.telegraf.utilities.initContacts
import com.example.telegraf.database.initFirebase
import com.example.telegraf.database.initUser

import com.example.telegraf.utilities.replaceFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    lateinit var toolbar: Toolbar;
    lateinit var mAppDrawer: AppDrawer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        APP_ACTIVITY = this;

        initFirebase()

        initUser {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts();
            }
            initFields();
            initFunc();
        };
    }

    private fun initFields() {
        toolbar = binding.mainToolbar;
        mAppDrawer = AppDrawer();
    }

    private fun initFunc() {
        setSupportActionBar(toolbar);
        if (AUTH.currentUser != null) {
            mAppDrawer.create();
            replaceFragment(ChatsFragment(), false)
        } else {
            replaceFragment(EnterPhoneNumberFragment(), false);
        }
    }

    override fun onStart() {
        super.onStart();
        if (AUTH.currentUser != null)
            AppState.updateState(AppState.ONLINE);
    }

    override fun onStop() {
        super.onStop();
        if (AUTH.currentUser != null)
            AppState.updateState(AppState.OFFLINE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initContacts()
        }
    }
}