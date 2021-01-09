package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.*

class AddRecipeActivity : AppCompatActivity() {

    lateinit var serverSelect : Spinner
    lateinit var professionSelect : Spinner
    lateinit var recipeListView : RecyclerView
    lateinit var confirmButton : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        serverSelect = findViewById<Spinner>(R.id.serverSelectSpinner) as Spinner
        val exampleServers = arrayOf("Please_select_server, Tichondrias, Illidan, Laughing-Skull, Area52")
        serverSelect.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,exampleServers)

        professionSelect = findViewById<Spinner>(R.id.professionSelectSpinner) as Spinner

        recipeListView = findViewById<RecyclerView>(R.id.recyclerView) as RecyclerView

        confirmButton = findViewById<Button>(R.id.confirmButton) as Button
        confirmButton.setOnClickListener {
            val homeActivityIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeActivityIntent)
        }
    }
}