package com.example.telegraf.utilities

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.telegraf.R
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.ui.fragments.ChatsFragment

fun Fragment.showToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
}

fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity::class.java)
    this.startActivity(intent);
    this.finish() // чтобы активити из которого вызвалась эта функция было завершено, а не висело в стеке
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, addToStack: Boolean = true) {
    if (addToStack) {
        this.supportFragmentManager
            .beginTransaction()
            .replace(R.id.dataContainer, fragment)
            .addToBackStack(null)
            .commit();
    } else {
        this.supportFragmentManager
            .beginTransaction()
            .replace(R.id.dataContainer, fragment)
            .commit();
    }
}

fun Fragment.replaceFragment(fragment: Fragment) {
    this.parentFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .addToBackStack(null)
        .commit();
}

fun Fragment.addMenuProvider(@MenuRes menuRes: Int, callback: (id: Int) -> Boolean) {
    val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(menuRes, menu);
        }

        override fun onMenuItemSelected(menuItem: MenuItem) = callback(menuItem.itemId);
    }
    (requireActivity() as MenuHost).addMenuProvider(
        menuProvider,
        viewLifecycleOwner,
        Lifecycle.State.RESUMED
    )
}
