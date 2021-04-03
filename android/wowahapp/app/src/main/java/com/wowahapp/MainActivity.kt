package com.wowahapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.BaseCallback
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.auth0.android.lock.Lock
import com.auth0.android.lock.Lock.newBuilder
import com.auth0.android.lock.LockCallback
import com.auth0.android.lock.utils.LockException
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var account : Auth0
    private lateinit var lock : Lock

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the account settings
        account = Auth0(
                getString(R.string.com_auth0_clientId),
                getString(R.string.com_auth0_domain)
        )

        account.isOIDCConformant = true
        lock = newBuilder(account, callback)
                .withAudience("https://wowahapp.us.auth0.com/userinfo")
                //.withScheme(getString(R.string.com_auth0_scheme))
                .withAuthStyle("My Theme", R.style.Lock_Theme_AuthStyle)

                .build(this)

        startActivity(lock.newIntent(this))
    }

    private fun showUserProfile(accessToken: String) {
        var client = AuthenticationAPIClient(account)

        // With the access token, call `userInfo` and get the profile from Auth0.
        client.userInfo(accessToken)
                .start(object : BaseCallback<UserProfile, AuthenticationException> {
                    override fun onSuccess(payload: UserProfile?) {
                        // We have the user's profile!
                        val email = payload?.email
                        val name = payload?.nickname
                    }
                    override fun onFailure(error: AuthenticationException) {
                        Toast.makeText(this@MainActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private val callback : LockCallback =  object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            //Authenticated
            val accessToken = credentials.accessToken
            Toast.makeText(this@MainActivity, "Logged in", Toast.LENGTH_SHORT).show()


            var client = AuthenticationAPIClient(account)

            // With the access token, call `userInfo` and get the profile from Auth0.
            credentials?.accessToken?.let {
                client.userInfo(it)
                    .start(object : BaseCallback<UserProfile, AuthenticationException> {
                        override fun onSuccess(payload: UserProfile?) {
                            // We have the user's profile!
                            payload?.email?.let { (application as CustomApplication).setUserName(it) }

                            val homeIntent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(homeIntent)
                            if (accessToken != null) {
                                showUserProfile(accessToken)
                            }

                        }
                        override fun onFailure(error: AuthenticationException) {
                            Toast.makeText(this@MainActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()
                        }
                    })
            }


        }
        override fun onCanceled() {
            //User pressed back
            // Exit the Application
            moveTaskToBack(true)
            exitProcess(-1)
        }
        override fun onError(error: LockException) {
            Toast.makeText(this@MainActivity, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}