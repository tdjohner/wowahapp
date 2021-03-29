package com.wowahapp

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

object UserDataService {

    fun subscribeRecipe(username: String, recipeName: String, realmID: String, applicationContext: Context) {
        val url = "http://192.168.0.24:49155/subscriberecipe/"
        val params = HashMap<String,String>()
        //These two parameters are pulled from the UI/ recipe objects.
        params["recipeName"] = recipeName
        params["username"] = username
        params["realmID"] = realmID

        val jsonObject = JSONObject(params as Map<*, *>)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener{},
            Response.ErrorListener {
                    error -> error.printStackTrace()
            }
        )
        VolleyWebService.getInstance(applicationContext).addToRequestQueue(request)
    }
}