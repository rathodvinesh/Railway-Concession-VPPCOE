package com.example.railwayconcession.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.railwayconcession.R
import com.example.railwayconcession.activities.MainActivity
import com.example.railwayconcession.databinding.FragmentHomeBinding
import com.example.railwayconcession.activities.forms
import com.example.railwayconcession.firebaseConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.Calendar
import java.util.Objects
import javax.security.auth.callback.Callback

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
//    private var imageView: ImageView? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)


        //nav
        navController = findNavController()

        // clg id
        var auth: FirebaseAuth = Firebase.auth
        val email = auth.currentUser?.email
        val clgId = email?.substring(0,11)?.toUpperCase()

        val dbref = firebaseConfig.userRef.child("$clgId/CLIST/")


        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val greet = when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..23 -> "Good Evening"
            else -> "Good Night"
        }

        binding.greet.text = "$greet"


        binding.progressBar.visibility = View.VISIBLE
//        binding.homeImgDesc1.visibility = View.VISIBLE


        val timeForRetrieve = ArrayList<String>()

        firebaseConfig.userRef.child("$clgId/CLIST").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        binding.progressBar.visibility = View.GONE
                        val key = snapshot.key
                        if (key != null) {
                            timeForRetrieve.add(key)
//                            Toast.makeText(this@concession_form, key, Toast.LENGTH_SHORT).show()
                        }else{
                            binding.progressBar.visibility = View.GONE
                            val home1ImageView = view?.findViewById<ImageView>(R.id.home_img)

                            home1ImageView?.setImageResource(
                                R.drawable.calender
                            )
                            binding.homeImgDesc1.visibility = View.VISIBLE
                            binding.homeImgDesc2.visibility = View.GONE
                        }
                    }

                    // Get the last element from the list
                    val lastTime = timeForRetrieve.lastOrNull()

                    if (lastTime != null) {
                        firebaseConfig.userRef.child("$clgId/CLIST/$lastTime/concessionPeriod").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    binding.progressBar.visibility = View.GONE

                                    val concessionPeriod = snapshot.getValue(String::class.java)

                                    val myImageView = view?.findViewById<ImageView>(R.id.home_img)

//                                    myImageView?.setImageResource(R.drawable.calender)
//                                    binding.homeImgDesc1.visibility = View.VISIBLE
//                                    binding.homeImgDesc2.visibility = View.GONE

                                    // Handle image changes based on the concession period
                                    when (concessionPeriod) {
                                        "Monthly" -> {
//                                            applyBtnDisable()
                                            myImageView?.setImageResource(R.drawable._27_days)
                                            binding.homeImgDesc1.visibility = View.GONE
                                            binding.homeImgDesc2.visibility = View.VISIBLE
                                        }
                                        "Quarterly" -> {
                                            applyBtnDisable()
                                            myImageView?.setImageResource(R.drawable._83_days)
                                            binding.homeImgDesc1.visibility = View.GONE
                                            binding.homeImgDesc2.visibility = View.VISIBLE
                                        }
                                        else -> {
                                            applyBtnDisable()
                                            myImageView?.setImageResource(R.drawable._335_days)
                                            binding.homeImgDesc1.visibility = View.GONE
                                            binding.homeImgDesc2.visibility = View.VISIBLE
                                        }
//                                        else -> {
//                                            myImageView?.setImageResource(R.drawable.calender)
//                                            binding.homeImgDesc1.visibility = View.VISIBLE
//                                            binding.homeImgDesc2.visibility = View.GONE
//                                        }
                                    }



                                }else{
                                    binding.progressBar.visibility = View.GONE
                                    val home2ViewImage = view?.findViewById<ImageView>(R.id.home_img)
                                    home2ViewImage?.setImageResource(
                                        R.drawable.calender
                                    )
                                    binding.homeImgDesc1.visibility = View.VISIBLE
                                    binding.homeImgDesc2.visibility = View.GONE
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle the error
                                val home3ImageView = view?.findViewById<ImageView>(R.id.home_img)
                                home3ImageView?.setImageResource(
                                    R.drawable.calender
                                )
                                binding.homeImgDesc1.visibility = View.VISIBLE
                                binding.homeImgDesc2.visibility = View.GONE
                            }
                        })
                    }
                }
                else{
                    val home4ImageView= view?.findViewById<ImageView>(R.id.home_img)
                    home4ImageView?.setImageResource(
                        R.drawable.calender
                    )
                    binding.homeImgDesc1.visibility = View.VISIBLE
                    binding.homeImgDesc2.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Cannot get key", Toast.LENGTH_SHORT).show()
                val home5ImageView= view?.findViewById<ImageView>(R.id.home_img)
                home5ImageView?.setImageResource(
                    R.drawable.calender
                )
                binding.homeImgDesc1.visibility = View.VISIBLE
                binding.homeImgDesc2.visibility = View.GONE
            }
        })

        firebaseConfig.userRef.child("$clgId/USERD/fullname").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullname = snapshot.getValue(String::class.java)
                    val home6ImageView = view?.findViewById<ImageView>(R.id.home_img)
                if (fullname != null) {
                    binding.name.text = fullname.toString()
                    binding.progressBar.visibility = View.GONE

                    home6ImageView?.setImageResource(
                        R.drawable.calender
                    )
                    binding.homeImgDesc1.visibility = View.VISIBLE
                    binding.homeImgDesc2.visibility = View.GONE
                } else {
                    binding.name.text = ""
                    binding.progressBar.visibility = View.GONE

                    home6ImageView?.setImageResource(
                        R.drawable.calender
                    )
                    binding.homeImgDesc1.visibility = View.VISIBLE
                    binding.homeImgDesc2.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
                val home7ImageView = view?.findViewById<ImageView>(R.id.home_img)
                home7ImageView?.setImageResource(
                    R.drawable.calender
                )
                binding.homeImgDesc1.visibility = View.VISIBLE
                binding.homeImgDesc2.visibility = View.GONE
            }
        })


        binding.btnApplyConcession.setOnClickListener {

//            val intent = Intent(this.context, forms::class.java)
//            startActivity(intent)
//
            navController.navigate(R.id.action_home2_to_forms)
        }

        binding.homeImgDesc2.setOnClickListener{
//            navController.navigate(R.id.action_home2_to_views)



//            navController.addOnDestinationChangedListener { controller, destination, arguments ->
//                if (destination.id == R.id.views) {
//                    // Set the selected destination as "views" in the bottom navigation view
//                    // Assuming you have a BottomNavigationView with an ID of "bottomNavigationView"
//                    binding.bottomNavigationView.selectedItemId = R.id.views
//                }
//            }
            Toast.makeText(requireContext(),"Click on Views",Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }
    fun applyBtnDisable(){
        binding.btnApplyConcession.isEnabled = false
        binding.btnApplyConcession.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#d3d4fa"))
//        binding.btnApplyConcession.setTextColor(ContextCompat.getColor(requireContext(), R.color.disabledText))
    }

}