package com.wowahapp

import android.app.Application

class CustomApplication : Application() {
    private lateinit var userName: String

    fun getUserName(): String {
        return userName
    }

    fun setUserName(uname: String) {
        userName = uname
    }
}