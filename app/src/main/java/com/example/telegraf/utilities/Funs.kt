package com.example.telegraf.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.telegraf.MainActivity
import com.example.telegraf.R
import com.example.telegraf.database.updatePhonesToDatabase
import com.example.telegraf.models.CommonModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show();
}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent);
    APP_ACTIVITY.finish()
}

fun replaceFragment(fragment: Fragment, addToStack: Boolean = true) {
    if (addToStack) {
        APP_ACTIVITY.supportFragmentManager
            .beginTransaction()
            .replace(R.id.data_container, fragment)
            .addToBackStack(null)
            .commit();
    } else {
        APP_ACTIVITY.supportFragmentManager
            .beginTransaction()
            .replace(R.id.data_container, fragment)
            .commit();
    }
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
fun hideKeyboard(){
    val imm: InputMethodManager = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0);
}
fun ImageView.downloadAndSetImage(url: String){
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(R.drawable.default_photo)
        .into(this)
}
@SuppressLint("Range")
fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        val contacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        cursor?.let{cursor
            while(cursor.moveToNext()){
                val fullName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel();
                newModel.fullname = fullName;
                newModel.phone = phone.replace(Regex("[\\s, -]"), "")
                contacts.add(newModel)
            }
        }
        updatePhonesToDatabase(contacts);
        cursor?.close();
    }
}
fun String.asTime(): String {
    val time = Date(this.toLong());
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time);
}

@SuppressLint("Range")
fun getFilenameFromUri(uri: Uri): String {
    var filename = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)
    try{
        if((cursor != null) && cursor.moveToFirst()){
            filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    }catch(e: Exception){
        e.message.toString()
    }finally{
        cursor?.close()
    }
    return filename;
}
