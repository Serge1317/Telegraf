package com.example.telegraf.utilities

import com.example.telegraf.database.CHILD_STATE
import com.example.telegraf.database.NODE_USERS
import com.example.telegraf.database.REF_DATABASE_ROOT
import com.example.telegraf.database.UID
import com.example.telegraf.database.USER

enum class AppState(val state: String) {
    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("пишет...");

    companion object{
        fun updateState(appState: AppState){
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_STATE)
                .setValue(appState.state)
                .addOnCompleteListener{
                    USER.state = appState.state;
                }.addOnFailureListener{
                    it.message.toString()
                }
        }
    }
}