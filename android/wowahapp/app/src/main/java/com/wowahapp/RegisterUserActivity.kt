package com.wowahapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.webkit.WebView
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
    private lateinit var context : Context
    lateinit var tosWebView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        //context variable explicitly saved to be passed into onClick override
        context = this
        tosWebView = findViewById<WebView>(R.id.tosWebView) as WebView
        tosWebView.visibility = View.GONE

        backToLogin = findViewById<TextView>(R.id.returnToLoginTextview) as TextView
        backToLogin.setOnClickListener{
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }

        //https://android--code.blogspot.com/2020/02/android-kotlin-ktx-clickablespan-example.html
        tos = SpannableString("By creating an account you agree to the Terms of Service.")
        tos[40..56] = object: ClickableSpan() {
            override fun onClick(p0: View) {
                p0.cancelPendingInputEvents()
                tosWebView.loadUrl("http://www.wowahapp.com/wowahapp_privacy_policy.html")
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.YELLOW
            }
        }
        tosCheckBox = findViewById<CheckBox>(R.id.termsCheckBox) as CheckBox
        tosCheckBox.text = tos
        tosCheckBox.movementMethod = LinkMovementMethod()
    }
}