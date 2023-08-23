package com.example.telegraf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.ActivityMainBinding
import com.example.telegraf.models.User
import com.example.telegraf.ui.fragments.ChatsFragment
import com.example.telegraf.ui.objects.AppDrawer
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AUTH
import com.example.telegraf.utilities.AppState
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.FOLDER_PROFILE_IMAGE
import com.example.telegraf.utilities.NODE_USERS
import com.example.telegraf.utilities.READ_CONTACTS
import com.example.telegraf.utilities.REF_DATABASE_ROOT
import com.example.telegraf.utilities.REF_STORAGE_ROOT
import com.example.telegraf.utilities.UID
import com.example.telegraf.utilities.USER
import com.example.telegraf.utilities.checkPermission
import com.example.telegraf.utilities.initContacts
import com.example.telegraf.utilities.initFirebase
import com.example.telegraf.utilities.initUser
import com.example.telegraf.utilities.replaceActivity
import com.example.telegraf.utilities.replaceFragment
import com.example.telegraf.utilities.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private lateinit var toolbar: Toolbar;
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
        mAppDrawer = AppDrawer(this, toolbar);
    }


    private fun initFunc() {
        if (AUTH.currentUser != null) {
            this.setSupportActionBar(toolbar);
            mAppDrawer.create();
            replaceFragment(ChatsFragment(), false)
        } else {
            replaceActivity(RegisterActivity())
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