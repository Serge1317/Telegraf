package com.example.telegraf.database

import android.annotation.SuppressLint
import android.net.Uri
import com.example.telegraf.R
import com.example.telegraf.models.CommonModel
import com.example.telegraf.models.User
import com.example.telegraf.utilities.APP_ACTIVITY
import com.example.telegraf.utilities.AppValueEventListener
import com.example.telegraf.utilities.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance();
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User();
    UID = AUTH.currentUser?.uid.toString();
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference;
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
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
    messageMap[CHILD_ID] = messageKey.toString();
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
        .addOnFailureListener {
            it.message.toString()
        }
}

fun sendMessageToGroup(
    message: String,
    groupId: String,
    typeText: String,
    clearInputField: () -> Unit
) {
    val refMessages = "$NODE_GROUPS/$groupId/$NODE_MESSAGES"
    val messageKey: String? = REF_DATABASE_ROOT.child(refMessages).push().key;

    val messageMap = hashMapOf<String, Any>();
    messageMap[CHILD_FROM] = UID;
    messageMap[CHILD_ID] = messageKey.toString();
    messageMap[CHILD_TEXT] = message;
    messageMap[CHILD_TYPE] = typeText;
    messageMap[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP;

    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString()).updateChildren(messageMap)
        .addOnSuccessListener {
            clearInputField()
        }
        .addOnFailureListener {
            it.message.toString()
        }
}

fun sendMessageAsFile(
    receivingUserId: String,
    fileUrl: String,
    messageKey: String,
    messageType: String,
    filename: String
) {
    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingUserId"
    val refDialogReceiving = "$NODE_MESSAGES/$receivingUserId/$UID"

    val messageMap = hashMapOf<String, Any>();
    messageMap[CHILD_FROM] = UID;
    messageMap[CHILD_ID] = messageKey;
    messageMap[CHILD_TYPE] = messageType;
    messageMap[CHILD_FILE_URL] = fileUrl;
    messageMap[CHILD_TEXT] = filename
    messageMap[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val dialogMap = hashMapOf<String, Any>()
    dialogMap["$refDialogUser/$messageKey"] = messageMap;
    dialogMap["$refDialogReceiving/$messageKey"] = messageMap;

    REF_DATABASE_ROOT.updateChildren(dialogMap)
        .addOnFailureListener {
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
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            it.message.toString();
        }
}

fun changeBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.resources.getString(R.string.toast_data_update));
            USER.bio = newBio;
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
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
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            it.message.toString();
        }
}

fun getMessageKey(contactId: String): String {
    val messageKey = REF_DATABASE_ROOT
        .child(NODE_MESSAGES)
        .child(UID)
        .child(contactId)
        .push().key.toString();
    return messageKey
}

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedId: String,
    messageType: String,
    filename: String = ""
) {

    val path = REF_STORAGE_ROOT
        .child(FOLDER_FILE_MESSAGE)
        .child(messageKey);

    putFileToStorage(uri, path) {
        getUrlFromStorage(path) { url: String ->
            sendMessageAsFile(receivedId, url, messageKey, messageType, filename)
        }
    }
}

fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(file)
        .addOnSuccessListener {
            function()
        }.addOnFailureListener {
            it.message.toString()
        }
}

fun saveToMainList(contactId: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$UID/$contactId"
    val refReceiver = "$NODE_MAIN_LIST/$contactId/$UID"

    val mapUser = hashMapOf<String, Any>();
    mapUser[CHILD_ID] = contactId;
    mapUser[CHILD_TYPE] = type

    val mapReceiver = hashMapOf<String, Any>()
    mapReceiver[CHILD_ID] = UID;
    mapReceiver[CHILD_TYPE] = type

    val commonMap = hashMapOf<String, Any>();
    commonMap[refUser] = mapUser;
    commonMap[refReceiver] = mapReceiver;

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener {
            it.message.toString();
        }
}

fun clearChat(contactId: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(contactId)
        .removeValue()
        .addOnFailureListener { it.message.toString() }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(contactId).child(UID)
                .removeValue()
                .addOnSuccessListener {
                    function();
                }
        }
}

fun deleteChat(contactId: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID).child(contactId)
        .removeValue()
        .addOnFailureListener { it.message.toString() }
        .addOnSuccessListener {
            function()
        }
}

@SuppressLint("SuspiciousIndentation")
fun createGroupToDatabase(
    groupName: String,
    groupPhotoUri: Uri,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val groupKey = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString();
    val pathToDatabase = REF_DATABASE_ROOT.child(NODE_GROUPS).child(groupKey)
    val pathToStorage = REF_STORAGE_ROOT.child(FOLDER_GROUP_IMAGE).child(groupKey)

    val mapData = hashMapOf<String, Any>();
    mapData[CHILD_ID] = groupKey;
    mapData[CHILD_FULLNAME] = groupName;
    mapData[CHILD_PHOTO_URL] = "empty"

    val mapMembers = hashMapOf<String, Any>();
    listContacts.forEach {
        mapMembers[it.id] = USER_MEMBER;
    }
    mapMembers[UID] = USER_CREATOR;
    mapData[NODE_MEMBERS] = mapMembers;

    pathToDatabase.updateChildren(mapData)
        .addOnFailureListener {
            it.message.toString()
        }.addOnSuccessListener {
            if (groupPhotoUri != Uri.EMPTY) {
                putFileToStorage(groupPhotoUri, pathToStorage) {
                    getUrlFromStorage(pathToStorage) {
                        pathToDatabase.child(CHILD_PHOTO_URL).setValue(it)
                        addGroupToChatList(mapData, listContacts) {
                            function()
                        }
                    }
                }
            } else {
                addGroupToChatList(mapData, listContacts) {
                    function()
                }
            }
        }
}

fun addGroupToChatList(
    mapData: HashMap<String, Any>,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val pathToChatList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()
    map[CHILD_ID] = mapData[CHILD_ID].toString();
    map[CHILD_TYPE] = TYPE_GROUP;
    listContacts.forEach {
        pathToChatList.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    pathToChatList.child(UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener {
            function()
        }.addOnFailureListener { it.message.toString() }
}
