package com.example.telegraf.utilities

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.telegraf.R
import com.example.telegraf.activities.RegisterActivity
import com.example.telegraf.ui.fragments.ChatsFragment

fun Fragment.showToast(message: String){
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
}
fun AppCompatActivity.replaceActivity(activity: AppCompatActivity){
    val intent = Intent(this, activity::class.java)
    this.startActivity(intent);
    this.finish() // чтобы активити из которого вызвалась эта функция было завершено, а не висело в стеке
}
fun AppCompatActivity.replaceFragment(fragment: Fragment){
    this.supportFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .addToBackStack(null)
        .commit();
}
fun Fragment.replaceFragment(fragment: Fragment){
    this.parentFragmentManager
        .beginTransaction()
        .replace(R.id.dataContainer, fragment)
        .addToBackStack(null)
        .commit();
}