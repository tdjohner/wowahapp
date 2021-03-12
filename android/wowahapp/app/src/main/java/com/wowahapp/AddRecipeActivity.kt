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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


interface PriceInterface {
    fun onCallback(response : String)
}

interface AuctionNameInterface {
    fun onCallback(response : String)
}

class AddRecipeActivity : AppCompatActivity(), PriceInterface, AuctionNameInterface {

    interface MyCallback {
        fun onValueChanged()
    }

    lateinit var searchTextView : TextView
    lateinit var professionSpinner : Spinner
    lateinit var expansionSpinner : Spinner
    lateinit var recipeRecycler : RecyclerView
    private lateinit var recipeAdapter : CustomAdapter
    private val recipeNames = ArrayList<String>()
    private val recipeList = ArrayList<RecipeModel>()

    val myInterface = this

    override fun onCallback(response : String) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        searchTextView = findViewById<TextView>(R.id.searchTextView) as TextView
        professionSpinner = findViewById<Spinner>(R.id.professionSelectSpinner) as Spinner
        expansionSpinner = findViewById<Spinner>(R.id.expansionSelectSpinner) as Spinner
        getAllProfessions(professionSpinner)
        getAllExpansions(expansionSpinner)

        recipeRecycler = findViewById(R.id.recipeRecycler)
        recipeAdapter = CustomAdapter(this.recipeList)
        recipeRecycler.adapter = recipeAdapter
        recipeRecycler.layoutManager = LinearLayoutManager(applicationContext)

        getAllRecipes(recipeNames)

        /* Here we create the recipeModels and add them to the Adapter
        for (i in 0 until recipeNames.size) {
            //first we get the lowest current AH price of the item, if available
            val recipeModel = RecipeModel(recipeNames[i], "0.00", "0.00", "some note")
            recipeAdapter.addItem(recipeModel)
            //getItemPriceAH(recipeNames[i])
        }

         */


    }

    fun getItemListing(itemName : String, realmID : String) : Int {
        val url = "https://wowahapp.com:443/itemlisting/" + itemName.replace(" ", "%20") + "/" +  realmID // crappy URL encoding
        var unitPrice : Int = 0
        var buyout : Int = 0
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener {
                    response -> try {
                        unitPrice = response.getString("UnitPrice").toInt()
                        buyout = response.getString("Buyout").toInt()
                        myInterface.onCallback((unitPrice + buyout).toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            },
            Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
        return unitPrice.toInt() + buyout.toInt()
    }

    fun getAllRecipes(recipeList : ArrayList<String>) {
        val url = "https://wowahapp.com/allrecipes/"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener {
                response -> try {
                    var itemName : String
                    var listedPrice : String
                    var vendorPrice : String
                    var note : String
                    for (i in 0 until response.length()) {
                        /*!! The Recycler View gets updated each time we add a recipeModel to the recipeAdapter.
                        The Volley http calls are made asynchronously which means we can't reference data objects
                        outside their respective requests, since they may not be populated. However, with nested
                        requests I think we actually leverage that asynchronous behavior to our advantage.
                         */
                        //Declare all needed variables with their default values:
                        itemName = response.getString(i).split(":")[2].replace("\"","").replace("}", "")
                        listedPrice = "Err"
                        vendorPrice = "Err"
                        note = "Note"

                        //listedPrice = getItemListing(itemName, "76").toString()
                        val recipeModel = RecipeModel(itemName, listedPrice, vendorPrice, note)
                        recipeAdapter.addItem(recipeModel)

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

    fun getAllProfessions(professionSpinner : Spinner) {
        val url = "https://wowahapp.com/allprofessions"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener {
                response -> try {
                    var profArray : ArrayList<String> = ArrayList<String>()
                    for (i in 0 until response.length()) {
                        profArray.add(response.getString(i))
                    }
                    professionSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, profArray)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> error.printStackTrace() })
            VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

    fun getAllExpansions(expansionSpinner : Spinner) {
        val url = "https://wowahapp.com/allexpansions"
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

