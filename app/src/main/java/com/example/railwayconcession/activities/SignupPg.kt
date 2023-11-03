package com.example.railwayconcession.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.railwayconcession.R
import com.example.railwayconcession.fragments.Home

class SignupPg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_pg)

        val etName: EditText = findViewById(R.id.et_name)
        val etPass: EditText = findViewById(R.id.et_password)
        val btnLogin: Button = findViewById(R.id.btn_signup)
        val tvSignIn: TextView = findViewById(R.id.tv_sign_in)

        btnLogin.setOnClickListener {

            val gmail: String = etName.text.toString()
            val password: String = etPass.text.toString()
            val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"""
            val passRegex =
                """^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+{}\[\]:;<>,.?~\-]).{8,}$"""

            if (gmail.isNotEmpty() && password.isNotEmpty()) {
                if (gmail.matches(emailRegex.toRegex())) {
                    if (password.matches(passRegex.toRegex())) {
                        Toast.makeText(this, "Sign up Successful", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, Home::class.java))
                        finish()

                    } else {
                        Toast.makeText(this, "Enter strong password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill empty fields", Toast.LENGTH_LONG).show()
            }
        }

        tvSignIn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}