package com.wowahapp

import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import android.os.Vibrator
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var registerUser : TextView
    lateinit var forgotPass : TextView
    private lateinit var account : Auth0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize vibration
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Initialize the account settings
        account = Auth0(
            getString(R.string.com_auth0_clientId),
            getString(R.string.com_auth0_domain)
        )

        // https://stackoverflow.com/questions/47298935/handling-enter-key-on-edittext-kotlin-android
        editPassword.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                sendLoginRequest()
                // For now we just immediately go to the Home Activity
//                val homeIntent = Intent(this, HomeActivity::class.java)
//                startActivity(homeIntent)

                //hash username into var
                //hash password into var
                return@OnKeyListener true
            }
            false
        })

        registerUser = findViewById<TextView>(R.id.registerTextView) as TextView
        registerUser.setOnClickListener{
            val registerUserActivityIntent = Intent(this, RegisterUserActivity::class.java)
            startActivity(registerUserActivityIntent)
        }
        forgotPass = findViewById<TextView>(R.id.forgotPassTextView) as TextView
        forgotPass.setOnClickListener{
            val forgotPassActivityIntent = Intent(this, ForgotPass::class.java)
            startActivity(forgotPassActivityIntent)
        }

        scrollingBackground()
        //sendJson sends a json object to our backend
        //sendJson()
    }

    // Validate user using Auth0 service
    private fun sendLoginRequest() {
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(this,  object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(error: AuthenticationException) {
                    Toast.makeText(this@MainActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()
                }

                // Called when authentication completed successfully
                override fun onSuccess(result: Credentials) {
                    // Get the access token from the credentials object.
                    // This can be used to call APIs
                    val accessToken = result.accessToken

                    val homeIntent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(homeIntent)

                    Toast.makeText(this@MainActivity, "Logged in", Toast.LENGTH_SHORT).show()
                    showUserProfile(accessToken)

                }
            })
    }

    private fun showUserProfile(accessToken: String) {
        var client = AuthenticationAPIClient(account)

        // With the access token, call `userInfo` and get the profile from Auth0.
        client.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    Toast.makeText(this@MainActivity, "\"Failure: ${error.getCode()}\"", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: UserProfile) {
                    // We have the user's profile!
                    val email = result.email
                    val name = result.nickname
                    Toast.makeText(this@MainActivity, email + "\n" + name, Toast.LENGTH_SHORT).show()
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

}