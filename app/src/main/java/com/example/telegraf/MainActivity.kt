package com.example.telegraf

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.databinding.ActivityMainBinding
import com.example.telegraf.ui.fragments.ChatsFragment
import com.example.telegraf.ui.objects.AppDrawer
import com.example.telegraf.utilities.replaceActivity
import com.example.telegraf.utilities.replaceFragment

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
        // если пользователь не зарегистрировался то перекидаем его на активити с регистрацией (там два фрагмента)
        if(true){
            this.setSupportActionBar(toolbar); // сначала setSupport а потом create (иначе не работает бутерброд)
            mAppDrawer.create();
            replaceFragment(ChatsFragment())
        }else{
            replaceActivity(RegisterActivity())
        }

    }
}