package com.example.telegraf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.telegraf.databinding.ActivityMainBinding
import com.example.telegraf.ui.fragments.ChatsFragment
import com.example.telegraf.ui.objects.AppDrawer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    private lateinit var toolbar: Toolbar;
    private lateinit var mAppDrawer: AppDrawer;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }
    override fun onStart(){
        super.onStart();
        initFields();
        initFunc();
    }
    private fun initFields(){
        toolbar = binding.mainToolbar;
        mAppDrawer = AppDrawer(this, toolbar);
    }

    private fun initFunc() {
        this.setSupportActionBar(toolbar); // сначала setSupport а потом create (иначе не работает бутерброд)
        mAppDrawer.create();
        this.supportFragmentManager.beginTransaction()
            .replace(R.id.dataContainer, ChatsFragment())
            .commit();
    }



}