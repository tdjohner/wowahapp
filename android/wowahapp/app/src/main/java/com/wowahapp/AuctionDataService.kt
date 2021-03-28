package com.wowahapp

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject


class AuctionDataService {

    interface VolleyResponseListener {
        fun onResponse(response : String)
        fun onError(error : String)
    }

    interface ArrayListListener {
        fun onResponse(response : ArrayList<String>)
        fun onError(error : String)
    }

    interface RealmListListener {
        fun onResponse(response : Map<String, Int>)
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
        request.setRetryPolicy(
            DefaultRetryPolicy(
                1000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
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
        val request = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener { response -> try {
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

    fun getAllServers(applicationContext: Context, responseListener: RealmListListener) {
        val url = "http://192.168.0.24:49155/allservers"
        var tmp: JSONObject
        val request = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener { response -> try {
            var servers : MutableMap<String, Int> = emptyMap<String, Int>().toMutableMap()
            for (i in 0 until response.length()) {
                tmp = JSONObject(response.getString(i))
                servers[tmp.getString("RealmName")] = tmp.getString("CnctdRealmID").toInt()
            }
            responseListener.onResponse(servers)
        } catch (e: JSONException ) {
            e.printStackTrace()
        }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

    fun getRecipeBaseCost(recipeName: String, realmID: String, applicationContext: Context, responseListener: VolleyResponseListener) {
        val url = "https://wowahapp.com/recipebasecost/"+recipeName.replace(" ", "%20")+"/"+realmID

        val request = StringRequest(Request.Method.GET, url, Response.Listener<String> { response -> try {
            responseListener.onResponse(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }

}