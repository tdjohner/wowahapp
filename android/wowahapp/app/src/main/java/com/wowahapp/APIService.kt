package com.wowahapp

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    //Needs the url for which is waiting for the post.
    @POST("/createuser/")
    suspend fun createEmployee(@Body requestBody: RequestBody): Response<ResponseBody>

}