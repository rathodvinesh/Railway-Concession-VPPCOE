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
import com.example.railwayconcession.R
import com.example.railwayconcession.databinding.ActivityConcessionFormBinding
import com.example.railwayconcession.firebaseConfig
import com.example.railwayconcession.fragments.Home
import com.example.railwayconcession.model.Users
import com.example.railwayconcession.model.userConccessionDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

//        val clgId = intent.getStringExtra("clgId")
//        Log.i("Info", "$clgId")

        auth = Firebase.auth

        val currentUser = auth.currentUser

        var auth: FirebaseAuth = Firebase.auth
        val email = auth.currentUser?.email
        val clgId = email?.substring(0,11)?.uppercase(Locale.ROOT)

//        etAppliedDate.text = Date
        btnSubmit.setOnClickListener {
            val voucherNo: String = etVoucherNo.text.toString()
//            val trainClass : RadioGroup = etClass.checkedRadioButtonId

            val checkedClassButton = etClass.checkedRadioButtonId
            val rgCheckedValue = findViewById<RadioButton>(checkedClassButton)
            val etclass = rgCheckedValue.text.toString()

            val concessionPeriod: String = etConcessionPeriod.selectedItem.toString()
            val appliedDate: String = etAppliedDate.text.toString()
            val source: String = etSource.selectedItem.toString()
            val destination: String = etDestination.selectedItem.toString()

            if (concessionPeriod.isNotEmpty() && appliedDate.isNotEmpty() && source.isNotEmpty() && destination.isNotEmpty() && voucherNo.isNotEmpty() && concessionPeriod.isNotEmpty()) {
                val users = userConccessionDetails(
                    voucherNo=voucherNo, concession_class = etclass , concessionPeriod = concessionPeriod , appliedDate= appliedDate, source = source, destination = destination
                )
                clgId?.let { it1 ->
                    firebaseConfig.createNewUserCListRef(it1).setValue(users)
            //                    database.child("Users").child(clgId).child("ConcessionDetails").setValue(users)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Data insert", Toast.LENGTH_SHORT).show()
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