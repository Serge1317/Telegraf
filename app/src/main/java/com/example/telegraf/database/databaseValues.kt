package com.example.telegraf.database

import com.example.telegraf.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth;
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var USER: User;
lateinit var UID: String;
lateinit var REF_STORAGE_ROOT: StorageReference;

const val TYPE_MESSAGE_TEXT = "text"
const val TYPE_MESSAGE_IMAGE = "image"
const val TYPE_MESSAGE_VOICE = "voice"
const val TYPE_MESSAGE_FILE = "file"

const val TYPE_CHAT = "chat"
const val TYPE_GROUP = "group"
const val TYPE_CHANNEL = "channel"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONE_CONTACTS = "phone_contacts"
const val NODE_MESSAGES = "messages"
const val NODE_MAIN_LIST = "main_list"

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_FILE_MESSAGE = "file_message"
const val NODE_GROUPS = "groups"
const val NODE_MEMBERS = "members"
const val FOLDER_GROUP_IMAGE = "group_image"
const val USER_CREATOR = "creator"
const val USER_MEMBER = "member"
const val USER_ADMIN = "admin"

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
const val CHILD_FILE_URL = "fileUrl"