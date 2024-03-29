package com.example.telegraf.utilities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val READ_CONTACTS: String = Manifest.permission.READ_CONTACTS
const val RECORD_AUDIO: String = Manifest.permission.RECORD_AUDIO;
const val WRITE_STORAGE: String = Manifest.permission.WRITE_EXTERNAL_STORAGE;
const val REQUEST_CODE = 120;

fun checkPermission(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= 23
        && ContextCompat.checkSelfPermission(APP_ACTIVITY, permission)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(APP_ACTIVITY, arrayOf(permission), REQUEST_CODE)
        false;
    } else {
        true;
    }
}