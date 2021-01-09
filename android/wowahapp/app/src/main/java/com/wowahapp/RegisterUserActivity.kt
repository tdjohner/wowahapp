package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class RegisterUserActivity : AppCompatActivity() {

    lateinit var backToLogin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        backToLogin = findViewById<TextView>(R.id.returnToLoginTextview) as TextView
        backToLogin.setOnClickListener{
            val loginActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(loginActivityIntent)
        }

    }
}