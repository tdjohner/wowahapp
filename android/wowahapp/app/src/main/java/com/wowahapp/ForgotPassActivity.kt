package com.wowahapp

import android.app.Activity
import android.content.Context
import android.content.Context.*
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import kotlinx.android.synthetic.main.activity_main.*

class ForgotPass : AppCompatActivity() {

    lateinit var backToLogin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        // https://stackoverflow.com/questions/47298935/handling-enter-key-on-edittext-kotlin-android
        editUsername.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                //close keyboard
                if (currentFocus != null) {
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }

                Toast.makeText(applicationContext, "Sending...", LENGTH_LONG).show()

                //TODO Trigger a password reset

                return@OnKeyListener true
            }
            false
        })

        backToLogin = findViewById<TextView>(R.id.backToLoginTextView)
        backToLogin.setOnClickListener {
            val homeActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(homeActivityIntent)
        }
    }
}