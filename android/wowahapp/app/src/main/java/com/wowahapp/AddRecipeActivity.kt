package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.*

class AddRecipeActivity : AppCompatActivity() {

    lateinit var serverSelect : Spinner
    lateinit var professionSelect : Spinner
    lateinit var recipeListView : RecyclerView
    lateinit var confirmButton : Button
    lateinit var dummyText : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        serverSelect = findViewById<Spinner>(R.id.serverSelectSpinner) as Spinner

        val exampleServers = arrayOf("Please_select_item", "Broken Fishing Pole", "Keen Incisor", "Blackthorn Warboots", "Potion of Deathly Fixation")
        serverSelect.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,exampleServers)

        professionSelect = findViewById<Spinner>(R.id.professionSelectSpinner) as Spinner

        recipeListView = findViewById<RecyclerView>(R.id.recyclerView) as RecyclerView

        confirmButton = findViewById<Button>(R.id.confirmButton) as Button
        confirmButton.setOnClickListener {

            dummyText = findViewById<TextView>(R.id.dummyTextView) as TextView
            dummyText.text = serverSelect.getItemAtPosition(serverSelect.selectedItemPosition).toString()

            //Volley.newRequestQueue(this)

            /* RE-IMPLEMENT AFTER DEMO!
            val homeActivityIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeActivityIntent)

            */
        }
    }
}