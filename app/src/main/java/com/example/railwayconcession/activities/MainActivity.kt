package com.example.railwayconcession.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.railwayconcession.R
import com.example.railwayconcession.databinding.ActivityMainBinding
import com.example.railwayconcession.firebaseConfig
import com.example.railwayconcession.firebaseConfig.currentDateTime
import com.example.railwayconcession.firebaseConfig.formattedDateTime
import com.example.railwayconcession.firebaseConfig.formatter
import com.example.railwayconcession.fragments.Home
import com.example.railwayconcession.fragments.Profile
import com.example.railwayconcession.fragments.Views
import com.example.railwayconcession.model.userConccessionDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

        private lateinit var binding:ActivityMainBinding
        private lateinit var navController: NavController
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            //        setContentView(R.layout.activity_main)
            binding= ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

//            val navHostFragment = supportFragmentManager
//                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//
//            // Initialize the NavController
//            val navController = navHostFragment.navController

//            binding.bottomNavigation.setOnItemSelectedListener { menuItem->
//                when(menuItem){
//                    R.id.home2 -> findNavController().navigate(ac)
//                }
//            }

//            val currentDateTime = LocalDateTime.now()
//            val formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm")
//            Log.d("Current Date and Time,", "$formattedDateTime")

//            val formattedDateTime = currentDateTime.format(formatter)


            val data = firebaseConfig.userRef.child("VU1F2122010/CLIST")
            val userConcessionList = mutableListOf<userConccessionDetails>()

            data.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle data retrieval here
                    for (dateTimeSnapshot in dataSnapshot.children) {
                        // Iterate over each date and time node
                        val dateTime = dateTimeSnapshot.key // Get the date and time as a string

                        if (dateTime != null) {
                            println("Date and Time: $dateTime")

                            val fieldValue = dateTimeSnapshot.getValue(userConccessionDetails::class.java)

                            if (fieldValue != null) {
                                // Add the UserConcessionDetails object to the list
                                userConcessionList.add(fieldValue)
                            }

                            Log.d("data",userConcessionList.toString())
                            // Iterate over the children under the date and time node
                            for (childSnapshot in dateTimeSnapshot.children) {
                                val childName = childSnapshot.key
                                val childData = childSnapshot.getValue() // Get the data inside each child node
                                if (childData != null) {
                                    // Use 'childData' as needed
//                                    Log.d("Child Data:", "$childName - $childData")
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
//            Log.d("data",userConcessionList.toString())
            replaceFragment(Home())

            setUpFragment()

        }



        private fun setUpFragment() {
            binding.bottomNavigation.setItemSelected(R.id.nav_home)
            binding.bottomNavigation.setOnItemSelectedListener {
                when(it){
                    R.id.nav_home ->replaceFragment(Home())
                    R.id.nav_view ->replaceFragment(Views())
                    R.id.nav_profile ->replaceFragment(Profile())

                    else -> replaceFragment(Home())
                }
            }
        }

        private fun replaceFragment(home: Fragment) {
            val fragManager = supportFragmentManager
            val fragTransaction = fragManager.beginTransaction()
            fragTransaction.replace(R.id.nav_host_fragment,home)
            fragTransaction.commit()
        }



}