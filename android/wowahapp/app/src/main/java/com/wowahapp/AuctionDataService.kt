package com.wowahapp

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import java.math.BigDecimal
import java.math.BigInteger

class AuctionDataService {

    interface VolleyResponseListener {
        fun onResponse(response : String)
        fun onError(error : String)
    }

    interface ArrayListListener {
        fun onResponse(response : ArrayList<String>)
        fun onError(error : String)
    }

    fun getAllRecipes(realmID : String, applicationContext : Context, recipeListListener : ArrayListListener ) {
        val url = "https://wowahapp.com/allrecipes/" + realmID
        
        var recipeList = ArrayList<String>()
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response -> try {
                for (i in 0 until response.length()) {
                    recipeList.add( response.getString(i).split(":")[2].replace("\"","").replace("}", ""))
                }
                recipeListListener.onResponse(recipeList)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            },
            Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

    fun getItemListing(itemName : String, realmID : String, applicationContext : Context, responseListener : VolleyResponseListener) {
        val url = "https://wowahapp.com:443/itemlisting/" + itemName.replace(" ", "%20") + "/" +  realmID // crappy URL encoding
        var unitPrice : Double
        var buyoutPrice : Double
        var listingPrice : Double
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                    response -> try {
                unitPrice = response.getString("UnitPrice").toDouble()
                buyoutPrice = response.getString("Buyout").toDouble()
                listingPrice = (unitPrice + buyoutPrice)/10000
                responseListener.onResponse(String.format("%.2f", listingPrice))
            } catch (e: JSONException) {
                responseListener.onError(e.toString())
            }
            },
            Response.ErrorListener { error -> error.printStackTrace() }
        )
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

    fun getAllProfessions(applicationContext: Context, responseListener : ArrayListListener) {
        val url = "https://wowahapp.com/allprofessions"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener {
                    response -> try {
                var profArray : ArrayList<String> = ArrayList<String>()
                for (i in 0 until response.length()) {
                    profArray.add(response.getString(i))
                }
                responseListener.onResponse(profArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            },
            Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

    fun getAllExpansions(applicationContext: Context, responseListener: ArrayListListener) {
        val url = "https://wowahapp.com/allexpansions"
        val request = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener { response ->try {
            var expArray : ArrayList<String> = ArrayList<String>()
            for (i in 0 until response.length()) {
                expArray.add(response.getString(i))
            }
            responseListener.onResponse(expArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }


}