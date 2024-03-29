package com.wowahapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Context
import android.os.SystemClock
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView



class AddRecipeActivity : AppCompatActivity() {

    lateinit var searchTextView : SearchView
    lateinit var serverSelectSpinner : Spinner
    lateinit var recipeRecycler : RecyclerView
    lateinit var confirmButton : Button
    lateinit var serverMap : Map<String, Int>
    private lateinit var recipeAdapter : CustomAdapterShopping
    private lateinit var customAdapterDetails: CustomAdapterDetails
    private val recipeList = ArrayList<RecipeModel>()
    private val detailList = ArrayList<DetailedEntries>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        val auctionDataService = AuctionDataService()

        searchTextView = findViewById<SearchView>(R.id.searchView) as SearchView
        serverSelectSpinner = findViewById<Spinner>(R.id.serverSelectSpinner) as Spinner
        confirmButton = findViewById<Button>(R.id.confirmButton) as Button
        recipeRecycler = findViewById(R.id.recipeRecycler)
        recipeRecycler.layoutManager = LinearLayoutManager(applicationContext)
        recipeAdapter = CustomAdapterShopping(recipeList)
        recipeRecycler.adapter = recipeAdapter

        recipeAdapter.setOnClick(object : RecyclerviewCallbacks<RecipeModel> {
            override fun onItemClick(view: View, position: Int, item: RecipeModel){
                //This is just to populate it to test
                val reagentList: ArrayList<ArrayList<String>> = ArrayList(ArrayList())
                val reagent: ArrayList<String> = ArrayList()
                val availableList: ArrayList<ArrayList<String>> = ArrayList(ArrayList())
                val available: ArrayList<String> = ArrayList()
                available.add("${item.getAverageSalePrice()}")
                availableList.add(available)
                available.add("${item.getSalePrice()}")
                available.add("${item.getAverageSalePrice()}")
                availableList.add(available)
                reagent.add("${item.getRecipeName()}")
                reagent.add("${item.getAverageSalePrice()}")
                reagentList.add(reagent)

                //Both of these lists are ArrayList<ArrayList<String>>
                //You want the first list(where reagentList is) entered here to be lists of reagents and their amounts
                //second list is lists of amount available, name, and cost
                //headers and string value changes can be made in DetailedView.kt in the getEntries function
                val detailedView : DetailedView=DetailedView("${item.getRecipeName()}", reagentList, availableList)
                showDetail("${item.getRecipeName()}",detailedView)

            }})

        serverSelectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var realmID = serverMap[parent?.getItemAtPosition(position)]

                recipeList.clear()
                recipeRecycler?.adapter?.notifyDataSetChanged()
                auctionDataService.getAllRecipes(realmID.toString(), applicationContext, object : AuctionDataService.RecipeHandleArrayListener {
                    override fun onResponse(response: ArrayList<RecipeHandle>) {
                        // now we loop over the recipe names and populate the RecipeModel objects then add them to the adapter
                        for (r in response) {
                            var model = RecipeModel(r.getName(), "x", "0.0", "0.0", r.getURL(), realmID)
                            auctionDataService.getItemListing(r.getName(), realmID.toString(), applicationContext, object : AuctionDataService.VolleyResponseListener {
                                override fun onResponse(response: String) {
                                    val saleprice = response
                                    model.setAverageSalePrice(saleprice)
                                    auctionDataService.getRecipeBaseCost(r.getName(), realmID.toString(), applicationContext, object : AuctionDataService.VolleyResponseListener {
                                        override fun onResponse(response: String) {
                                            val cost = response.toDouble()
                                            val sum: String
                                            if (cost.toFloat() > 0) { // only show recipes that can be filled on AH
                                                sum = calculateExchange(saleprice.toDouble(), cost)
                                                model.setSalePrice(String.format("%.2f", cost))
                                                model.setLink(sum)
                                                model.setProfitability()
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
            }
        }

        recipeAdapter.setOnClick(object : RecyclerviewCallbacks<RecipeModel> {
            override fun onItemClick(view: View, position: Int, item: RecipeModel){
                //This is just to populate it to test
                val reagentList: ArrayList<ArrayList<String>> = ArrayList(ArrayList())
                val reagent: ArrayList<String> = ArrayList()
                val availableList: ArrayList<ArrayList<String>> = ArrayList(ArrayList())
                val available: ArrayList<String> = ArrayList()

                auctionDataService.getListingDetails("${item.getRecipeName()}", "${item.getRealmID()}", applicationContext,
                    object: AuctionDataService.ReagentPairListener {
                        override fun onResponse(response: Pair<ArrayList<ArrayList<String>>, ArrayList<ArrayList<String>>>) {
                            val detailsPair = response
                            val detailedView = DetailedView("${item.getRecipeName()}", detailsPair.first, detailsPair.second)
                            showDetail("${item.getRecipeName()}",detailedView)
                        }
                        override fun onError(error: String) {
                            println("Error getting base recipe detailed info: " + error)
                        }
                    })
                }
            }
        )

        auctionDataService.getAllServers(applicationContext, object : AuctionDataService.RealmListListener {
            override fun onResponse(response: Map<String, Int>) {
                serverMap = response
                serverSelectSpinner.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, response.keys.toTypedArray())
            }
            override fun onError(error: String) {
                println("Error in getAllExpansions :" + error)
            }
        })

        confirmButton.setOnClickListener {
            for (r in recipeAdapter.getRecipeList()) {
                if (r.getIsSelected() == true) {
                    // Subscribe user to recipe
                    val subscriptionRealmID = serverMap[serverSelectSpinner.selectedItem].toString()
                    val recipeName = r.getRecipeName()
                    recipeName?.let { it1 ->
                        UserDataService.subscribeRecipe((application as CustomApplication).getUserName(), it1, subscriptionRealmID, applicationContext)
                    }
                }
            }
            SystemClock.sleep(2000)
            intent = Intent(this@AddRecipeActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        searchTextView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recipeAdapter.filter.filter(newText)
                return false
            }
        })

    }

    fun calculateExchange(rtrns: Double, cost: Double): String {
        val c = cost.toDouble()
        val r = rtrns.toDouble()
        var net = r - c
        return String.format("%.2f", net)
    }
    private fun showDetail(title: String, detailedView: DetailedView){
        var detailedEntries = ArrayList<DetailedEntries>()
        detailedEntries=detailedView.getEntries()
        val inflater=getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.recipe_details, null)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        customAdapterDetails = CustomAdapterDetails(detailList)
        customAdapterDetails.setItems(detailedEntries)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapterDetails
        val title = view.findViewById<TextView>(R.id.recipeName)
        title.text=detailedView.getTitle()

        val popUp = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        popUp.isOutsideTouchable=true
        popUp.isFocusable=true
        popUp.showAtLocation(view, Gravity.CENTER,0,0)
    }
}

