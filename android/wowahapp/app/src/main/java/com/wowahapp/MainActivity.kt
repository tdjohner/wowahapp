package com.wowahapp

import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // https://stackoverflow.com/questions/47298935/handling-enter-key-on-edittext-kotlin-android
        editPassword.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //hash username into var
                //hash password into var
                    sendLoginRequest()
                return@OnKeyListener true
            }
            false
        })


        scrollingBackground();
    }

    // Validate user against user database
    private fun sendLoginRequest() {


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