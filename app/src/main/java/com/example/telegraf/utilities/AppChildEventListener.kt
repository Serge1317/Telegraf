package com.example.telegraf.utilities

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class AppChildEventListener(private val childAdded: (DataSnapshot) -> Unit): ChildEventListener {

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        childAdded(snapshot);
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
       //TODO("Not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        //TODO("Not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        //TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        //TODO("Not yet implemented")
    }
}