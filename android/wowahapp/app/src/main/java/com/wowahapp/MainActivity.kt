package com.wowahapp

import android.animation.ValueAnimator
import android.content.Intent
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
import androidx.annotation.RequiresApi
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
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
                .closable(true)
                //builder.withScope("openid profile email")
                //.allowedConnections(Arrays.asList("google-oauth2"))
                //.withScheme(getString(R.string.com_auth0_scheme))
                .withAuthStyle("My Theme", R.style.Lock_Theme_AuthStyle)

                .build(this)

        startActivity(lock.newIntent(this))
        //sendLoginRequest ()
//
//                // https://stackoverflow.com/questions/47298935/handling-enter-key-on-edittext-kotlin-android
//                editPassword . setOnKeyListener (View.OnKeyListener { _, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
//
//                return@OnKeyListener true
//            }
//            false
//        })
//
//                registerUser = findViewById < TextView >(R.id.registerTextView) as TextView
//                registerUser . setOnClickListener {
//            val registerUserActivityIntent = Intent(this, RegisterUserActivity::class.java)
//            startActivity(registerUserActivityIntent)
//        }
//                forgotPass = findViewById < TextView >(R.id.forgotPassTextView) as TextView
//                forgotPass . setOnClickListener {
//            val forgotPassActivityIntent = Intent(this, ForgotPass::class.java)
//            startActivity(forgotPassActivityIntent)
//        }
//
//                scrollingBackground ()
        //sendJson sends a json object to our backend
        //sendJson()
    }

    // Validate user using Auth0 service
//    private fun sendLoginRequest() {
//        WebAuthProvider.login(account)
//                .withScheme(getString(R.string.com_auth0_scheme))
//                .withScope("openid profile email")
//                // Launch the authentication passing the callback where the results will be received
//                .start(this,  object : Callback<Credentials, AuthenticationException> {
//                    // Called when there is an authentication failure
//                    @RequiresApi(Build.VERSION_CODES.O)
//                    override fun onFailure(error: AuthenticationException) {
//                        Toast.makeText(this@MainActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()
//
//                        // Vibrate the device
//                        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
//                        if (v.hasVibrator()) {
//                            val vEffect: VibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
//                            v.cancel()
//                            v.vibrate(vEffect)
//                        }
//
//                        // Exit the Application
//                        moveTaskToBack(true)
//                        exitProcess(-1)
//                    }
//
//                    // Called when authentication completed successfully
//                    override fun onSuccess(result: Credentials) {
//                        // Get the access token from the credentials object.
//                        // This can be used to call APIs
//                        val accessToken = result.accessToken
//
//                        val homeIntent = Intent(this@MainActivity, HomeActivity::class.java)
//                        startActivity(homeIntent)
//
//                        Toast.makeText(this@MainActivity, "Logged in", Toast.LENGTH_SHORT).show()
//                        showUserProfile(accessToken)
//
//                    }
//                })
//    }
//
//    private fun showUserProfile(accessToken: String) {
//        var client = AuthenticationAPIClient(account)
//
//        // With the access token, call `userInfo` and get the profile from Auth0.
//        client.userInfo(accessToken)
//                .start(object : Callback<UserProfile, AuthenticationException> {
//                    override fun onFailure(error: AuthenticationException) {
//                        Toast.makeText(this@MainActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onSuccess(result: UserProfile) {
//                        // We have the user's profile!
//                        val email = result.email
//                        val name = result.nickname
//                        Toast.makeText(this@MainActivity, email + "\n" + name, Toast.LENGTH_SHORT).show()
//                    }
//                })
//    }

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

//    override fun onDestroy() {
//        super.onDestroy()
//        // Your own Activity code
//        if (lock != null) {
//            lock.onDestroy(this)
//        }
//        //lock = null
//    }

    private val callback : LockCallback =  object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            //Authenticated
            Toast.makeText(this@MainActivity, "Logged in" + credentials.accessToken, Toast.LENGTH_SHORT).show()
            val homeIntent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(homeIntent)
        }

        override fun onCanceled() {
            //User pressed back
            // Exit the Application
            moveTaskToBack(true)
            exitProcess(-1)
        }

        override fun onError(error: LockException) {

        }
    }
}