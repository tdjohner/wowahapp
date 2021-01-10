package com.wowahapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.set
import androidx.core.text.toSpannable

class RegisterUserActivity : AppCompatActivity() {

    lateinit var backToLogin : TextView
    lateinit var tosCheckBox : CheckBox
    lateinit var clickableSpan : ClickableSpan
    lateinit var tos : Spannable
    lateinit var tosActivity : RegisterUserActivity
    private lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        //context variable explicitly saved to be passed into onClick override
        context = this

        backToLogin = findViewById<TextView>(R.id.returnToLoginTextview) as TextView
        backToLogin.setOnClickListener{
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }

        //https://android--code.blogspot.com/2020/02/android-kotlin-ktx-clickablespan-example.html
        tos = SpannableString("By creating an account you agree to the Terms of Service.")
        tos[40..57] = object: ClickableSpan() {
            override fun onClick(p0: View) {
                val termsOfServiceIntent = Intent(context, TermsOfService::class.java)
                startActivity(termsOfServiceIntent)
            }
        }
        tosCheckBox = findViewById<CheckBox>(R.id.termsCheckBox) as CheckBox
        tosCheckBox.text = tos
        tosCheckBox.movementMethod = LinkMovementMethod()
    }
}