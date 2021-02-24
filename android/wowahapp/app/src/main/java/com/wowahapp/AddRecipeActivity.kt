package com.wowahapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.json.JSONArray
import org.json.JSONException


class AddRecipeActivity : AppCompatActivity() {

    lateinit var searchTextView : TextView
    lateinit var professionSpinner : Spinner
    lateinit var expansionSpinner : Spinner
    private var requestQueue : RequestQueue? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)


        searchTextView = findViewById<TextView>(R.id.searchTextView) as TextView
        professionSpinner = findViewById<Spinner>(R.id.professionSelectSpinner) as Spinner
        expansionSpinner = findViewById<Spinner>(R.id.expansionSelectSpinner) as Spinner
        getAllProfessions(professionSpinner)
        getAllExpansions(expansionSpinner)

    }

    fun getAllProfessions(professionSpinner : Spinner) {

        val url = "http://192.168.0.24:49155/allprofessions"
        val request = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener { response ->try {
            var profArray : ArrayList<String> = ArrayList<String>()
            for (i in 0 until response.length()) {
                profArray.add(response.getString(i))
            }
            professionSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, profArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            }, Response.ErrorListener { error -> error.printStackTrace() })
            VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

    fun getAllExpansions(expansionSpinner : Spinner) {
        val url = "http://192.168.0.24:49155/allexpansions"
        val request = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener { response ->try {
            var expArray : ArrayList<String> = ArrayList<String>()
            for (i in 0 until response.length()) {
                expArray.add(response.getString(i))
            }
            expansionSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }
}

