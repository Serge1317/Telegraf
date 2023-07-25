package com.example.telegraf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.ActivityMainBinding
import com.example.telegraf.models.User
import com.example.telegraf.ui.fragments.ChatsFragment
import com.example.telegraf.ui.objects.AppDrawer
import com.example.telegraf.utilities.AUTH
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.NODE_USER
import com.example.telegraf.utilities.REF_DATABASE_ROOT
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
    }
    override fun onStart(){
        super.onStart();
        initFields();
        initFunc();
        initUser();
    }
    private fun initFields(){
        toolbar = binding.mainToolbar;
        mAppDrawer = AppDrawer(this, toolbar);
        initFirebase()
    }

    private fun initFunc() {
        if(AUTH.currentUser != null){
            this.setSupportActionBar(toolbar); // сначала setSupport а потом create (иначе не работает бутерброд)
            mAppDrawer.create();
            replaceFragment(ChatsFragment(), false)
        }else{
            replaceActivity(RegisterActivity())
        }
    }
    private fun initUser(){
        REF_DATABASE_ROOT.child(NODE_USER).child(UID)
            .addListenerForSingleValueEvent(AppValueEventListener{
            USER  = it.getValue(User::class.java) ?: User();
        })
    }
}