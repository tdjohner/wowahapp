package com.wowahapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.wowahapp.AuctionDataService
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AddRecipeActivity : AppCompatActivity() {

    lateinit var searchTextView : TextView
    lateinit var professionSpinner : Spinner
    lateinit var serverSelectSpinner : Spinner
    lateinit var recipeRecycler : RecyclerView
    private lateinit var recipeAdapter : CustomAdapter
    private val recipeList = ArrayList<RecipeModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        val auctionDataService = AuctionDataService()

        searchTextView = findViewById<TextView>(R.id.searchTextView) as TextView
        professionSpinner = findViewById<Spinner>(R.id.professionSelectSpinner) as Spinner
        serverSelectSpinner = findViewById<Spinner>(R.id.serverSelectSpinner) as Spinner
        recipeRecycler = findViewById(R.id.recipeRecycler)
        recipeAdapter = CustomAdapter(this.recipeList)
        recipeRecycler.adapter = recipeAdapter
        recipeRecycler.layoutManager = LinearLayoutManager(applicationContext)

        val realmID = "76"

        auctionDataService.getAllRecipes(realmID, applicationContext, object : AuctionDataService.ArrayListListener {
            override fun onResponse(response: ArrayList<String>) {
                // now we loop over the recipe names and populate the RecipeModel objects then add them to the adapter
                for (r in response) {
                    var model = RecipeModel(r, "x", "x", "note")
                    auctionDataService.getItemListing(r, realmID, applicationContext, object : AuctionDataService.VolleyResponseListener {
                        override fun onResponse(response: String) {
                            val saleprice = response
                            model.setAverageSalePrice(saleprice)
                            auctionDataService.getRecipeBaseCost(r, realmID, applicationContext, object : AuctionDataService.VolleyResponseListener {
                                override fun onResponse(response: String) {
                                    val cost = response.toDouble()
                                    val sum: String
                                    if (cost.toFloat() > 0) { // only show recipes that can be filled on AH
                                        sum = calculateExchange(saleprice.toDouble(), cost)
                                        model.setSalePrice(String.format("%.2f", cost))
                                        model.setLink(sum)
                                        recipeAdapter.addItem(model)
                                    }
                                }
                                override fun onError(error: String) {
                                    println("Error getting base recipe cost: " + error)
                                }
                            })
                        }
                        override fun onError(error: String) {
                            println("Error getting price listing data: " + error)
                        }
                    })
                }
            }
            override fun onError(error: String) {
                println("Error in getAllRecipes: " + error)
            }
        })

        auctionDataService.getAllProfessions(applicationContext, object : AuctionDataService.ArrayListListener {
            override fun onResponse(response: ArrayList<String>) {
                professionSpinner.adapter =  ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, response)
            }
            override fun onError(error: String) {
                println("Error in getAllProfessions :" + error)
            }
        })

        auctionDataService.getAllServers(applicationContext, object : AuctionDataService.ArrayListListener {
            override fun onResponse(response: ArrayList<String>) {
                serverSelectSpinner.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, response)
            }
            override fun onError(error: String) {
                println("Error in getAllExpansions :" + error)
            }
        })


    }

    fun calculateExchange(rtrns: Double, cost: Double): String {
        val c = cost.toDouble()
        val r = rtrns.toDouble()
        var net = r - c
        return String.format("%.2f", net)
    }
}

