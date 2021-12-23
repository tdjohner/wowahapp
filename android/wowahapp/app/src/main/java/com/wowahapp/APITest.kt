package com.wowahapp

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit


fun main(args: Array<String>)  {
    sendJson()
}

//Code from https://johncodeos.com/how-to-make-post-get-put-and-delete-requests-with-retrofit-using-kotlin/
fun sendJson(){
    // Create our retrofit
    val retrofit = Retrofit.Builder()
            //The ip address is wrong, it needs to be changed
        .baseUrl("http://35.193.69.215:49155")
        .build()

    //Create the service
    val service = retrofit.create(APIService::class.java)

    //create JSON
    val jsonObject = JSONObject()
    //Here we get user input for the fields we want, from input fields
    jsonObject.put("name","Sean")
    jsonObject.put("email","liens5@mymacewan.ca")

    //Convert our JSONObject to string
    val jsonObjectString = jsonObject.toString()

    val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

    CoroutineScope(Dispatchers.IO).launch {
        val response = service.createEmployee(requestBody)

        withContext(Dispatchers.Main){
            if (response.isSuccessful){
                // If our server sends a response, we get a log here
                // for now we have no response, but the server reads it.
                val gson = GsonBuilder().setPrettyPrinting().create()
                val prettyJson = gson.toJson(JsonParser.parseString(response.body()?.string()))
                Log.d("Pretty Printed JSON :", prettyJson)

            } else {
                Log.e("RETROFIT_ERROR", response.code().toString())

            }
        }
    }
}
