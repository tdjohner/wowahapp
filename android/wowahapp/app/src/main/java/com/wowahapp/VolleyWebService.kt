package com.wowahapp

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.RequestQueue.RequestFilter
import com.android.volley.toolbox.Volley

/*
    This class is an implementation of this tutorial:
    http://code.sunnyjohn.in/index.php/2020/12/24/retrieve-data-volley/
    which was suggested as a response to this question:
    https://stackoverflow.com/questions/66298527/android-volley-request-utterly-fails
 */
class VolleyWebService constructor(context: Context) {
    private var INSTANCE: VolleyWebService? = null

    companion object {
        @Volatile
        private var INSTANCE: VolleyWebService? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleyWebService(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    fun clearRequestQueue() {
        requestQueue.cancelAll({ true })
    }
}