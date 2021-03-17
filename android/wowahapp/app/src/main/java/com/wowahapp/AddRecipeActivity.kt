package com.wowahapp

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
    lateinit var expansionSpinner : Spinner
    lateinit var recipeRecycler : RecyclerView
    private lateinit var recipeAdapter : CustomAdapter
    private val recipeNames = ArrayList<String>()
    private val recipeList = ArrayList<RecipeModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        val auctionDataService = AuctionDataService()

        searchTextView = findViewById<TextView>(R.id.searchTextView) as TextView
        professionSpinner = findViewById<Spinner>(R.id.professionSelectSpinner) as Spinner
        expansionSpinner = findViewById<Spinner>(R.id.expansionSelectSpinner) as Spinner
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
                    auctionDataService.getItemListing(r, "76", applicationContext, object : AuctionDataService.VolleyResponseListener {
                        override fun onResponse(response: String) {
                            model.setSalePrice(response)
                        }
                        override fun onError(error: String) {
                            println("Error getting price listing data: " + error)
                        }
                    })
                    recipeAdapter.addItem(model)
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

        auctionDataService.getAllExpansions(applicationContext, object : AuctionDataService.ArrayListListener {
            override fun onResponse(response: ArrayList<String>) {
                expansionSpinner.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, response)
            }
            override fun onError(error: String) {
                println("Error in getAllExpansions :" + error)
            }
        })
        /* Here we create the recipeModels and add them to the Adapter
        for (i in 0 until recipeNames.size) {
            //first we get the lowest current AH price of the item, if available
            val recipeModel = RecipeModel(recipeNames[i], "0.00", "0.00", "some note")
            recipeAdapter.addItem(recipeModel)
            //getItemPriceAH(recipeNames[i])
        }

         */
    }
}

