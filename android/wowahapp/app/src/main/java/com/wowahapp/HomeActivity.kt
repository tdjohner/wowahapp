package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HomeActivity : AppCompatActivity() {

    lateinit var addRecipeButton : Button
    lateinit var logoutTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        addRecipeButton = findViewById<Button>(R.id.addRecipeButton) as Button
        addRecipeButton.setOnClickListener{
            val addRecipeIntent = Intent(this, AddRecipeActivity::class.java)
            startActivity(addRecipeIntent)
        }

        logoutTextView = findViewById<TextView>(R.id.logoutTextView)
        logoutTextView.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

    }
}