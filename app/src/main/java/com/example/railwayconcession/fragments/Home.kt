package com.example.railwayconcession.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.railwayconcession.R
import com.example.railwayconcession.databinding.FragmentHomeBinding
import com.example.railwayconcession.activities.forms
import com.example.railwayconcession.firebaseConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Objects

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private var imageView: ImageView? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)

        // clg id
        var auth: FirebaseAuth = Firebase.auth
        val email = auth.currentUser?.email
        val clgId = email?.substring(0,11)?.toUpperCase()

        val dbref = firebaseConfig.userRef.child("$clgId/CLIST/")

//        var name by remember { mutableStateOf() }
//        binding.name.text = clgId.toString()

//        val myImageView = view?.findViewById<ImageView>(R.id.home_img)
//
//        // To change the image to a new one:
//        myImageView?.setImageResource(R.drawable.daysleft)

//        imageView = view?.findViewById(R.id.home_img)
//
//        // Set a new image for the ImageView
//        imageView?.setImageResource(R.drawable.daysleft)

        binding.homeImgDesc1.visibility = View.VISIBLE

        binding.btnApplyConcession.setOnClickListener {

            val intent = Intent(this.context, forms::class.java)
            startActivity(intent)
        }

        firebaseConfig.userRef.child("$clgId/USERD/fullname").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullname = snapshot.getValue(String::class.java)
                if (fullname != null) {
                    binding.name.text = fullname.toString()

//                    val vectorDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.daysleft)
//                    binding.homeImg.setImageDrawable(R.drawable.daysleft)

                    val myImageView = view?.findViewById<ImageView>(R.id.home_img)

                    // To change the image to a new one:
                    myImageView?.setImageResource(R.drawable._27_days)

                    binding.homeImgDesc1.visibility = View.GONE
                    binding.homeImgDesc2.visibility = View.GONE

                } else {
                    binding.name.text = ""
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
            }
        })

//        val concessionPeriodRef = firebaseConfig.userRef.child("$clgId/CLIST/02_11_2023_19_12/concessionPeriod")
//
//        concessionPeriodRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val concessionPeriod = snapshot.getValue(String::class.java)
//
//                val myImageView = view?.findViewById<ImageView>(R.id.home_img)
//
//                // Handle image changes based on the concession period
//                when (concessionPeriod) {
//                    "monthly" -> myImageView?.setImageResource(R.drawable.monthly_image)
//                    "quarterly" -> myImageView?.setImageResource(R.drawable.quarterly_image)
//                    "yearly" -> myImageView?.setImageResource(R.drawable.yearly_image)
//                    else -> myImageView?.setImageResource(R.drawable.default_image)
//                }
//
//                // To change the image to a new one (example: _27_days):
//                myImageView?.setImageResource(R.drawable._27_days)
//
//                binding.homeImgDesc1.visibility = View.GONE
//                binding.homeImgDesc2.visibility = View.VISIBLE
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error
//            }
//        })


        return binding.root
    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//
//        navController = Navigation.findNavController(view)
//
//        super.onViewCreated(view, savedInstanceState)
//    }

}