package com.example.railwayconcession.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isNotEmpty
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.railwayconcession.R
import com.example.railwayconcession.activities.MainActivity
import com.example.railwayconcession.databinding.FragmentUpdateBinding
import com.example.railwayconcession.firebaseConfig
import com.example.railwayconcession.model.userConccessionDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class update : Fragment() {

    private lateinit var binding: FragmentUpdateBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateBinding.inflate(layoutInflater,container,false)

        navController = findNavController()

        val etVoucherNo: EditText = binding.etVoucherNo
        val etClass: RadioGroup = binding.rgClass
        val etConcessionPeriod = binding.spinnerConcessionPeriod
        val etAppliedDate: EditText = binding.etAppliedDate
        val etSource=binding.spinnerSource
        val etDestination=binding.spinnerDestination
        val btnUpdate: Button = binding.btnUpdate

        binding.updateProgressBar.visibility = View.VISIBLE

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDateTime = currentDateTime.format(formatter)
//    Log.d("Current Date and Time,", "$formattedDateTime")

        val editableDate = formattedDateTime.toString()
        val date = Editable.Factory.getInstance().newEditable(editableDate)

        binding.etAppliedDate.text = date

        etAppliedDate.isEnabled = false

//        val clgId = intent.getStringExtra("clgId")
//        Log.i("Info", "$clgId")

        auth = Firebase.auth

        val currentUser = auth.currentUser

        var auth: FirebaseAuth = Firebase.auth
        val email = auth.currentUser?.email
        val clgId = email?.substring(0,11)?.uppercase(Locale.ROOT)


        val timeForRetrieve = ArrayList<String>()

        firebaseConfig.userRef.child("$clgId/CLIST").addValueEventListener(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot in snapshot.children) {
                        val key = snapshot.key
                        if (key != null) {
                            timeForRetrieve.add(key)
//                            Toast.makeText(this@concession_form, key, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Get the last element from the list
                    val lastTime = timeForRetrieve.lastOrNull()

                    if (lastTime != null) {
                        firebaseConfig.userRef.child("$clgId/CLIST/$lastTime").addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val fbVoucherNo = snapshot.child("voucherNo").value.toString()
                                    val editableVoucherNo = Editable.Factory.getInstance().newEditable(fbVoucherNo)

                                    val fbClass = snapshot.child("concession_class").value.toString()

                                    when(fbClass){
                                        "l"->binding.rgClass.check(R.id.rgValI)
                                        else -> binding.rgClass.check(R.id.rgValII)
                                    }

                                    val fbSource = snapshot.child("source").value.toString()
                                    val indexSource = resources.getStringArray(R.array.source)

                                    binding.spinnerSource.setSelection(indexSource.indexOf(fbSource))

                                    val fbDestination = snapshot.child("destination").value.toString()
                                    val indexDestination = resources.getStringArray(R.array.destination)

                                    binding.spinnerDestination.setSelection(indexDestination.indexOf(fbDestination))

                                    val fbConPeriod = snapshot.child("concessionPeriod").value.toString()
                                    val indexConcPeriod = resources.getStringArray(R.array.concessionPeriod)

                                    binding.spinnerConcessionPeriod.setSelection(indexConcPeriod.indexOf(fbConPeriod))

                                    binding.etVoucherNo.text = editableVoucherNo

                                    binding.updateProgressBar.visibility = View.GONE

                                    etAppliedDate.isEnabled = false
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle the error
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Cannot get key", Toast.LENGTH_SHORT).show()
                binding.updateProgressBar.visibility = View.GONE

            }
        })



        btnUpdate.setOnClickListener {

            val voucherNo: String = etVoucherNo.text.toString()
            val trainClass : RadioGroup = binding.rgClass

            val concessionPeriod: String = etConcessionPeriod.selectedItem.toString()
            val appliedDate: String = etAppliedDate.text.toString()
            val source: String = etSource.selectedItem.toString()
            val destination: String = etDestination.selectedItem.toString()

            if (concessionPeriod.isNotEmpty() && trainClass.isNotEmpty() && appliedDate.isNotEmpty() && source.isNotEmpty() && destination.isNotEmpty() && voucherNo.isNotEmpty() && concessionPeriod.isNotEmpty()) {
                val checkedClassButton = etClass.checkedRadioButtonId
                val rgCheckedValue = binding.root.findViewById<RadioButton>(checkedClassButton)
                val etclass = rgCheckedValue.text.toString()

                val updatedData = mapOf(
                    "concessionClass" to etclass,
                    "voucherNo" to voucherNo,
                    "concessionPeriod" to concessionPeriod,
                    "appliedDate" to appliedDate,
                    "source" to source,
                    "destination" to destination
            )

                val lastDate = timeForRetrieve.lastOrNull()

                firebaseConfig.userRef.child("$clgId/CLIST/$lastDate").updateChildren(updatedData)
                    .addOnSuccessListener {
                        if(isAdded){
                            Toast.makeText(
                                requireContext(),
                                "Data updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        if(isAdded){
                            Toast.makeText(
                                requireContext(),
                                "Data update failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

        }
//
//                val users = userConccessionDetails(
//                    voucherNo=voucherNo, concessionClass = etclass , concessionPeriod = concessionPeriod , appliedDate= appliedDate, source = source, destination = destination
//                )
//                clgId?.let { it1 ->
//                    firebaseConfig.createNewUserCListRef(it1).setValue(users)
//                        //                    database.child("Users").child(clgId).child("ConcessionDetails").setValue(users)
//                        .addOnCompleteListener {
//                            Toast.makeText(requireContext(), "Data inserted", Toast.LENGTH_SHORT).show()
//
//                        }.addOnFailureListener {
//                            Toast.makeText(requireContext(), "Data not insert", Toast.LENGTH_SHORT).show()
//                        }
//                }
//            } else {
//                Toast.makeText(requireContext(), "Fill empty fields", Toast.LENGTH_SHORT).show()
//            }
//
//            val newName = binding.etName.text.toString()
//
//            // For RadioGroup, get the selected radio button's text
//            val selectedRadioButtonId = binding.rgGender.checkedRadioButtonId
//            val radioGroup = findViewById<RadioButton>(selectedRadioButtonId)
//            val newGender = radioGroup.text.toString()
//
//            // For Spinner, get the selected item
//            val newDept = binding.spinnerDept.selectedItem.toString()
//
//
//            // Assuming you have a unique key for the data you want to update (e.g., user key)
//            // val userKey = "your_unique_user_key"
//
//            val updatedData = mapOf(
//                "name" to newName,
//                "gender" to newGender,
//                "dept" to newDept
//            )
//
//            firebaseConfig.userRef.child("$clgId/CLIST/$timeForRetrieve/").updateChildren(updatedData)
//                .addOnSuccessListener {
//                    Toast.makeText(requireContext(), "Data updated successfully", Toast.LENGTH_SHORT).show()
//
//                }
//                .addOnFailureListener {
//                    Toast.makeText(requireContext(), "Data update failed", Toast.LENGTH_SHORT).show()
//                }
//
//        }

            navController.navigate(R.id.action_update_to_views)
        }


        return binding.root
    }
}