package com.example.railwayconcession

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
object firebaseConfig {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

     val rootReference: DatabaseReference by lazy {
        database.reference
    }
    val userRef: DatabaseReference by lazy {
        rootReference.child("USERS")
    }
//    val userDetailRef: DatabaseReference by lazy {
//        userRef.child("USERD")
//    }
//    val cListRef: DatabaseReference by lazy {
//        userRef.child("CLIST")
//    }

    fun createNewUserDetailsRef(clgID : String): DatabaseReference{
        val newUserDetailRef: DatabaseReference by lazy {
            userRef.child("$clgID/USERD")
        }
        return newUserDetailRef
    }

    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")
    val formattedDateTime = currentDateTime.format(formatter)
//    Log.d("Current Date and Time,", "$formattedDateTime")

    fun createNewUserCListRef(clgID : String): DatabaseReference{
        val newUserCListRef: DatabaseReference by lazy {
            userRef.child("$clgID/CLIST/$formattedDateTime")
        }
        return newUserCListRef
    }


}