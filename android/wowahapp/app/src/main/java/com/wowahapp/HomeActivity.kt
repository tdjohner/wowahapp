    package com.wowahapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import org.json.JSONObject
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {

    lateinit var addRecipeButton : Button
    lateinit var logoutTextView : TextView
    private val itemsList = ArrayList<RecipeModel>()
    private val detailList = ArrayList<DetailedEntries>()
    private lateinit var customAdapter: CustomAdapter
    private lateinit var customAdapterDetails: CustomAdapterDetails
    private lateinit var account : Auth0
    private var cachedCredentials: Credentials? = null
    private var cachedUserProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the account settings
        account = Auth0(
            getString(R.string.com_auth0_clientId),
            getString(R.string.com_auth0_domain)
        )
        addRecipeButton = findViewById<Button>(R.id.addRecipeButton) as Button
        addRecipeButton.setOnClickListener{
            val addRecipeIntent = Intent(this, AddRecipeActivity::class.java)
            startActivity(addRecipeIntent)
        }

        logoutTextView = findViewById<TextView>(R.id.logoutTextView)
        logoutTextView.setOnClickListener {
            // log user out
            logout()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView3)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        customAdapter.setOnClick(object : RecyclerviewCallbacks<RecipeModel> {
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
        prepareItems()


    }

    private fun prepareItems() {
        var recipe = RecipeModel("Spaghetti", "40.00","1", "3","https://render-us.worldofwarcraft.com/icons/56/inv_sword_39.jpg")
        customAdapter.addItem(recipe)
        recipe = RecipeModel("Noodles", "500.00","1", "2","x")
        customAdapter.addItem(recipe)
        recipe = RecipeModel("test","1","1","1","https://render-us.worldofwarcraft.com/icons/56/inv_sword_39.jpg")
        customAdapter.addItem(recipe)
        customAdapter.addItem(recipe)
        recipe = RecipeModel("test", "500.00","2", "1","x")
        customAdapter.addItem(recipe)
        recipe = RecipeModel("test","1","3","1","https://render-us.worldofwarcraft.com/icons/56/inv_sword_39.jpg")
        customAdapter.addItem(recipe)
    }
    private fun prepareDetails(){
        var detail = DetailedEntries(arrayListOf("test", "test", "test"))
        customAdapterDetails.addItem(detail)
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
        popUp.showAtLocation(view,Gravity.CENTER,0,0)
    }

    private fun logout() {
        cachedCredentials = null
        cachedUserProfile = null

        intent = Intent(this@HomeActivity, MainActivity::class.java)
        startActivity(intent)
//        WebAuthProvider.logout(account)
//            .withScheme(getString(R.string.com_auth0_scheme))
//            .start(this, object: Callback<Void?, AuthenticationException> {
//                @RequiresApi(Build.VERSION_CODES.O)
//                override fun onSuccess(result: Void?) {
//                    cachedCredentials = null
//                    cachedUserProfile = null
//
//                    Toast.makeText(this@HomeActivity, "Logged out", Toast.LENGTH_SHORT).show()
//
//                    // Go back to the main page
//                    intent = Intent(this@HomeActivity, MainActivity::class.java)
//                    startActivity(intent)
//                }
//
//                @RequiresApi(Build.VERSION_CODES.O)
//                override fun onFailure(error: AuthenticationException) {
//                    // Vibrate ethe device
//                    val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
//                    if (v.hasVibrator()) {
//                        val vEffect: VibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
//                        v.cancel()
//                        v.vibrate(vEffect)
//                    }
//
//                    Toast.makeText(this@HomeActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()
//
////                    // Exit the Application
////                    moveTaskToBack(true)
////                    exitProcess(-1)
//                }
//            })
    }

    private fun sendJsonObject(){
        val url = "https://wowahapp.com/createuser/"
        val params = HashMap<String,Int>()
        //These two parameters are pulled from the UI/ recipe objects.
        params["itemId"] = 3;
        params["userId"] = 4;

        val jsonObject = JSONObject(params as Map<*, *>)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                //Toast.makeText(this@HomeActivity, "Added Recipe Subscription", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener {
                    error -> error.printStackTrace()
            }
        )
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }
}