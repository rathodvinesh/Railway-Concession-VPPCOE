package com.example.railwayconcession.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.railwayconcession.R
import com.example.railwayconcession.databinding.FragmentPersonalFormBinding
import com.example.railwayconcession.model.Users
import com.example.railwayconcession.firebaseConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Locale

// this is initial activity
// stores the basic data
class forms : AppCompatActivity() {

//    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentPersonalFormBinding
    private lateinit var auth : FirebaseAuth
    private var count = 1111

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPersonalFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        database = FirebaseDatabase.getInstance().getReference("Users")
        auth = Firebase.auth

        val tvNext: TextView = binding.tvNext
        val etFullName: TextView = binding.etFullName
        val etAge: TextView = binding.etAge
        val spinnerDivision = binding.spinnerDivision
        val etClgId: TextView = binding.etClgId
        val rgGender = binding.rgGender
        val rgYear = binding.rgYear
        val spinnerDept = binding.spinnerDept
//        val srNo = binding.srNO

        val currentUser = auth.currentUser

        var auth: FirebaseAuth = Firebase.auth
        val email = auth.currentUser?.email
        val clgId = email?.substring(0,11)?.toUpperCase()

        etClgId.text = clgId.toString()
        etClgId.isEnabled = false

//            srNo.text = "Sr No. $count"

        firebaseConfig.userRef.child("$clgId/USERD").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    firebaseConfig.userRef.child("$clgId/USERD").get().addOnSuccessListener {
                        val editFName =snapshot.child("fullname").value.toString()
                        val fName = Editable.Factory.getInstance().newEditable(editFName)

                        val editAge =snapshot.child("age").value.toString()
                        val fAge = Editable.Factory.getInstance().newEditable(editAge)

                        binding.etFullName.text = fName
                        binding.etAge.text = fAge

                        val fGender = snapshot.child("gender").value.toString()

                        //checked button instance from fb
                        when(fGender){
                            "Male"-> rgGender.check(R.id.rgValMale)
                            "Female"-> rgGender.check(R.id.rgValFemale)
                            else -> rgGender.check(R.id.rgValOther)
                        }

                        val fYear = snapshot.child("year").value.toString()

                        when(fYear){
                            "F.E."-> rgYear.check(R.id.rgValFE)
                            "S.E."-> rgYear.check(R.id.rgValSE)
                            "T.E."-> rgYear.check(R.id.rgValTE)
                            else -> rgYear.check(R.id.rgValBE)
                        }

                        //SPinner

                        val fDept = snapshot.child("department").value.toString()

                        val deptArray = resources.getStringArray(R.array.department)

                        binding.spinnerDept.setSelection(deptArray.indexOf(fDept))

                        val fDivision = snapshot.child("division").value.toString()

                        val divArray = resources.getStringArray(R.array.division)

                        spinnerDivision.setSelection(divArray.indexOf(fDivision))
                    }.addOnFailureListener {
                        Toast.makeText(this@forms,"Data cannot be received",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        tvNext.setOnClickListener {

            val fullname = etFullName.text.toString()
            val age = etAge.text.toString()
            val division = spinnerDivision.selectedItem.toString()
            val clgId = etClgId.text.toString().uppercase(Locale.ROOT)
            val userId = currentUser?.uid
            val curSrNO = count.toString()

            val checkedGenderButton = rgGender.checkedRadioButtonId
            val rgGenderValue = findViewById<RadioButton>(checkedGenderButton)
            val gender = rgGenderValue.text.toString()

            val checkedYearButton = rgYear.checkedRadioButtonId
            val rgYearValue = findViewById<RadioButton>(checkedYearButton)
            val year = rgYearValue.text.toString()

            val department = spinnerDept.selectedItem.toString()

//            Toast.makeText(this@forms,"$gender , $year",Toast.LENGTH_SHORT).show()

            if (fullname.isNotEmpty() && age.isNotEmpty() && gender.isNotEmpty() && year.isNotEmpty() && department.isNotEmpty()) {
                val usersData = Users(fullname=fullname,age=age,division=division, clgId = clgId, gender = gender, year = year, department = department,userId= userId) // Use clgId here
                firebaseConfig.createNewUserDetailsRef(clgId).setValue(usersData) // Use clgId here
                    .addOnCompleteListener {
                        Toast.makeText(this, "Data insert", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, concession_form::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data not insert", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Fill empty fields", Toast.LENGTH_SHORT).show()
            }

            count++
        }
    }
}
