package com.example.telegraf.database

import android.net.Uri
import com.example.telegraf.R
import com.example.telegraf.models.CommonModel
import com.example.telegraf.models.User
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


lateinit var AUTH: FirebaseAuth;
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var USER: User;
lateinit var UID: String;
lateinit var REF_STORAGE_ROOT: StorageReference;
const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONE_CONTACTS = "phone_contacts"
const val NODE_MESSAGES = "messages"

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"


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
            if (USER.fullname.isEmpty()) {
                USER.fullname = UID;
            }
            if (USER.username.isEmpty()) {
                USER.username = UID;
            }
            function();
        })

}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
        it.children.forEach { snapshot ->
            arrayContacts.forEach { contact ->
                if (snapshot.key == contact.phone) {
                    REF_DATABASE_ROOT.child(NODE_PHONE_CONTACTS).child(UID)
                        .child(snapshot.value.toString())
                        .child(CHILD_ID)
                        .setValue(snapshot.value.toString())
                        .addOnFailureListener { e -> e.message.toString() }

                    REF_DATABASE_ROOT.child(NODE_PHONE_CONTACTS).child(UID)
                        .child(snapshot.value.toString())
                        .child(CHILD_FULLNAME)
                        .setValue(contact.fullname)
                        .addOnFailureListener { e -> e.message.toString() }
                }
            }
        }
    })
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel();

fun DataSnapshot.getUserModel(): User =
    this.getValue(User::class.java) ?: User();

fun sendMessage(
    message: String,
    receivingUserId: String,
    typeText: String,
    clearInputField: () -> Unit
) {
    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingUserId";
    val refDialogReceiveUser = "$NODE_MESSAGES/$receivingUserId/$UID";
    val messageKey: String? = REF_DATABASE_ROOT.child(refDialogUser).push().key;

    val messageMap = hashMapOf<String, Any>();
    messageMap[CHILD_FROM] = UID;
    messageMap[CHILD_TEXT] = message;
    messageMap[CHILD_TYPE] = typeText;
    messageMap[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP;

    val dialogMap = hashMapOf<String, Any>();
    dialogMap["$refDialogUser/$messageKey"] = messageMap;
    dialogMap["$refDialogReceiveUser/$messageKey"] = messageMap;

    REF_DATABASE_ROOT.updateChildren(dialogMap)
        .addOnSuccessListener {
            clearInputField()
        }
        .addOnFailureListener{
            it.message.toString()
        }
}
 fun changeUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newUserName).setValue(UID)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                updateCurrentUsername(newUserName)
            }
        }
}

private fun updateCurrentUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).setValue(newUserName)
        .addOnCompleteListener() {
            if (it.isSuccessful) {
                showToast(APP_ACTIVITY.resources.getString(R.string.toast_data_update))
                deleteOldUsername(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

private fun deleteOldUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
                showToast(APP_ACTIVITY.resources.getString(R.string.toast_data_update))
                USER.username = newUserName;
                //parentFragmentManager.popBackStack();
                APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener{
            it.message.toString();
        }
}

fun changeBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener{
                showToast(APP_ACTIVITY.resources.getString(R.string.toast_data_update));
                USER.bio = newBio;
                //this.parentFragmentManager.popBackStack();
                APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener{
            it.message.toString();
        }
}

fun changeNameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME)
        .setValue(fullname)
        .addOnSuccessListener {
                USER.fullname = fullname;
                APP_ACTIVITY.mAppDrawer.updateHeader();
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                //parentFragmentManager.popBackStack();
                APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener{
            it.message.toString();
        }
}
