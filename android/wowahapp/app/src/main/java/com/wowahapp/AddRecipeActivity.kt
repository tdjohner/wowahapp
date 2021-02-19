package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.*
import org.json.JSONObject
import java.net.URLEncoder

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
        professionSelect = findViewById<Spinner>(R.id.professionSelectSpinner) as Spinner
        recipeListView = findViewById<RecyclerView>(R.id.recyclerView) as RecyclerView
        confirmButton = findViewById<Button>(R.id.confirmButton) as Button

        val exampleServers = arrayOf("Please_select_item", "Broken Fishing Pole", "Keen Incisor", "Blackthorn Warboots", "Potion of Deathly Fixation")
        serverSelect.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,exampleServers)
        confirmButton.setOnClickListener {

            dummyText = findViewById<TextView>(R.id.dummyTextView) as TextView

            val q = Volley.newRequestQueue(this)

            //var url = "http://wowahapp.com/getitem/" + serverSelect.getItemAtPosition(serverSelect.selectedItemPosition).toString().replace(" ", "%20")
            val url = "http://wowahapp.com/getitem/Broken%20Fishing%20Pole/"
            println(url)
            val stringRequest = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener<JSONObject> { response ->
                val j = response.getString("Name")
                dummyText.text = j
                },
                Response.ErrorListener { error ->
                    println(error)
                })
            q.add(stringRequest)



            /* RE-IMPLEMENT AFTER DEMO!
            val homeActivityIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeActivityIntent)

            */
        }
    }
}