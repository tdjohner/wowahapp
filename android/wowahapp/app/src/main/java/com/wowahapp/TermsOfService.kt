package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.TextView
import org.w3c.dom.Text

class TermsOfService : AppCompatActivity() {

    lateinit var tosWebView : WebView
    lateinit var backToLoginTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_service)

        backToLoginTextView = findViewById<TextView>(R.id.backToLoginTextView) as TextView
        backToLoginTextView.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }

        tosWebView = findViewById<WebView>(R.id.tosWebView) as WebView
        tosWebView.loadUrl("http://www.wowahapp.com/wowahapp_privacy_policy.html")
    }
}