package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ForgotPass : AppCompatActivity() {

    lateinit var backToLogin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        backToLogin = findViewById<TextView>(R.id.backToLoginTextView)
        backToLogin.setOnClickListener{
            val homeActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(homeActivityIntent)
        }
    }
}