package com.example.railwayconcession.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toUpperCase
import androidx.core.view.isNotEmpty
import com.example.railwayconcession.R
import com.example.railwayconcession.databinding.ActivityConcessionFormBinding
import com.example.railwayconcession.firebaseConfig
import com.example.railwayconcession.fragments.Home
import com.example.railwayconcession.model.Users
import com.example.railwayconcession.model.userConccessionDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// this is actual concession form .
class concession_form : AppCompatActivity() {

//    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityConcessionFormBinding
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConcessionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etVoucherNo: EditText = findViewById(R.id.et_voucher_no)
        val etClass: RadioGroup = binding.rgClass
        val etConcessionPeriod = binding.spinnerConcessionPeriod
        val etAppliedDate: EditText = findViewById(R.id.et_applied_date)
        val etSource=binding.spinnerSource
        val etDestination=binding.spinnerDestination
        val btnSubmit: Button = findViewById(R.id.btn_submit)

        // Initialize Firebase database
//        database = FirebaseDatabase.getInstance().reference

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

        firebaseConfig.userRef.child("$clgId/CLIST").addValueEventListener(object : ValueEventListener {
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
                        firebaseConfig.userRef.child("$clgId/CLIST/$lastTime").addValueEventListener(object : ValueEventListener {
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
                Toast.makeText(this@concession_form, "Cannot get key", Toast.LENGTH_SHORT).show()
            }
        })


//        etAppliedDate.text = Date
        btnSubmit.setOnClickListener {
            val userId = currentUser?.uid
            val voucherNo: String = etVoucherNo.text.toString()
            val trainClass : RadioGroup = binding.rgClass


            val concessionPeriod: String = etConcessionPeriod.selectedItem.toString()
            val appliedDate: String = etAppliedDate.text.toString()
            val source: String = etSource.selectedItem.toString()
            val destination: String = etDestination.selectedItem.toString()

            if (concessionPeriod.isNotEmpty() && trainClass.isNotEmpty() && appliedDate.isNotEmpty() && source.isNotEmpty() && destination.isNotEmpty() && voucherNo.isNotEmpty() && concessionPeriod.isNotEmpty()) {
                val checkedClassButton = etClass.checkedRadioButtonId
                val rgCheckedValue = findViewById<RadioButton>(checkedClassButton)
                val etclass = rgCheckedValue.text.toString()
                val users = userConccessionDetails(
                    userId = userId,voucherNo=voucherNo, concessionClass = etclass , concessionPeriod = concessionPeriod , appliedDate= appliedDate, source = source, destination = destination,status = "Pending"
                )
                clgId?.let { it1 ->
                    firebaseConfig.createNewUserCListRef(it1).setValue(users)
            //                    database.child("Users").child(clgId).child("ConcessionDetails").setValue(users)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Data inserted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Data not insert", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Fill empty fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}