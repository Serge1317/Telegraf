package com.example.telegraf.utilities

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.ContactsContract
import com.example.telegraf.models.CommonModel
import com.example.telegraf.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


lateinit var AUTH: FirebaseAuth;
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var USER: User;
lateinit var UID: String;
lateinit var REF_STORAGE_ROOT: StorageReference;

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"


fun initFirebase() {
    AUTH = FirebaseAuth.getInstance();
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User();
    UID = AUTH.currentUser?.uid.toString();
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference;
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri).addOnSuccessListener {
        function();
    }.addOnFailureListener {
        showToast(it.message.toString())
    }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl.addOnSuccessListener {
        val url = it.toString();
        function(url)
    }.addOnFailureListener {
        showToast(it.message.toString())
    }
}

inline fun putUrlToDatabase(photoUrl: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .child(CHILD_PHOTO_URL)
        .setValue(photoUrl)
        .addOnSuccessListener {
            function();
        }.addOnFailureListener {
            it.message.toString();
        }
}
 inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(User::class.java) ?: User();
            if(USER.fullname.isEmpty()){
                USER.fullname = UID;
            }
            if(USER.username.isEmpty()){
                USER.username = UID;
            }
            function();
        })

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
                val fullname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel();
                newModel.fullname = fullname;
                newModel.phone = phone.replace(Regex("[\\s, -]"), "")
                contacts.add(newModel)
            }
        }
        cursor?.close();
    }
}