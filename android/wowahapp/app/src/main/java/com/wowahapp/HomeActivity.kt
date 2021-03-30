    package com.wowahapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
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
    private lateinit var customAdapter: CustomAdapter
    private lateinit var account : Auth0
    private var cachedCredentials: Credentials? = null
    private var cachedUserProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val auctionDataService = AuctionDataService()

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

        auctionDataService.getSubbedRecipes((application as CustomApplication).getUserName(), applicationContext, object: AuctionDataService.RecipeModelListener {
            override fun onResponse(response: ArrayList<RecipeModel>) {
                for (r in response) {
                    customAdapter.addItem(r)
                }
            }

            override fun onError(error: String) {
                println("Error getting subscribed item JSON: " + error)
            }
        })



    }

    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .start(this, object: Callback<Void?, AuthenticationException> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSuccess(result: Void?) {
                    cachedCredentials = null
                    cachedUserProfile = null

                    Toast.makeText(this@HomeActivity, "Logged out", Toast.LENGTH_SHORT).show()

                    // Go back to the main page
                    intent = Intent(this@HomeActivity, MainActivity::class.java)
                    startActivity(intent)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onFailure(error: AuthenticationException) {
                    // Vibrate ethe device
                    val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    if (v.hasVibrator()) {
                        val vEffect: VibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        v.cancel()
                        v.vibrate(vEffect)
                    }

                    Toast.makeText(this@HomeActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()

                    // Exit the Application
                    moveTaskToBack(true)
                    exitProcess(-1)
                }
            })
    }
}