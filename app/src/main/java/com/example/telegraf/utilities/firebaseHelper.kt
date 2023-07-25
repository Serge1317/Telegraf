package com.example.telegraf.utilities

import android.content.Context
import com.example.telegraf.models.User
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger


lateinit var AUTH: FirebaseAuth;
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var USER: User;
lateinit var UID: String;

const val NODE_USER = "users"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"



fun initFirebase(){
    AUTH = FirebaseAuth.getInstance();
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User();
    UID = AUTH.currentUser?.uid.toString();
}