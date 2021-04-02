package com.wowahapp

import android.animation.ValueAnimator
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.media.session.MediaSessionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.BaseCallback
import com.auth0.android.callback.Callback
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import kotlinx.android.synthetic.main.activity_main.*
import com.auth0.android.lock.Lock
import com.auth0.android.lock.Lock.newBuilder
import com.auth0.android.lock.LockCallback
import com.auth0.android.lock.utils.LockException
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    lateinit var registerUser : TextView
    lateinit var forgotPass : TextView
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
        //val builder : Lock.Builder = Lock.newBuilder(account, callback)
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

    // login wallpaper moving background from https://stackoverflow.com/questions/36894384/android-move-background-continuously-with-animation
    private fun scrollingBackground() {

        val backgroundOne = loginBackgroundOne
        val backgroundTwo= loginBackgroundTwo

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.duration = 10000L
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width: Int = backgroundOne.width
            val translationX = width * progress
            backgroundOne.setTranslationX(translationX)
            backgroundTwo.setTranslationX(translationX - width)
        }
        animator.start()
    }

    private val callback : LockCallback =  object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            //Authenticated
            val accessToken = credentials.accessToken
            Toast.makeText(this@MainActivity, "Logged in", Toast.LENGTH_SHORT).show()
            val homeIntent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(homeIntent)
            if (accessToken != null) {
                showUserProfile(accessToken)
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