package com.example.telegraf.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegraf.R
import com.example.telegraf.utilities.APP_ACTIVITY

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {
        override fun onStart(){
            super.onStart();
            APP_ACTIVITY.title = "Контакты"
        }
}