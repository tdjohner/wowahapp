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
        //println(recipeNames.size)

        /* Here we create the recipeModels and add them to the Adapter
        for (i in 0 until recipeNames.size) {
            //first we get the lowest current AH price of the item, if available
            val recipeModel = RecipeModel(recipeNames[i], "0.00", "0.00", "some note")
            recipeAdapter.addItem(recipeModel)
            //getItemPriceAH(recipeNames[i])
        }

         */


    }

    fun getItemPriceAH(name : String) : Int {
        var url = "http://192.168.0.24:49155/" + name +"/" + "76"//realmID
        url = url.replace(" ", "%20") // my crappy URL encoding
        var unitPrice : String = "0"
        var buyout : String = "0"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener {
                    response -> try {
                        //nasty parsing of returned JSON but it's functional
                        //price = response.getString(0).split(":")[2].replace("\"","").replace("}", ""))
                        println(response.getString(0).split(":")[2])
                        println(response.getString(0).split(":")[4])
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            },
            Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
        return unitPrice.toInt() + buyout.toInt()
    }

    fun getAllRecipes(recipeList : ArrayList<String>) {
        val url = "http://192.168.0.24:49155/allrecipes"
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

    fun getItemListing(itemName : String, realmID : String) : Int {
        val url = "http://192.168.0.24:49155/itemlisting/" + itemName.replace(" ", "%20") + realmID // crappy URL encoding
        return 7
    }

    fun getAllProfessions(professionSpinner : Spinner) {
        val url = "http://35.193.69.215:49155/allprofessions"
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
        val url = "http://35.193.69.215:49155/allexpansions"
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

