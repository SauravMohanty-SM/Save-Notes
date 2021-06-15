package com.barracudas.savenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var splashScreens: ImageView
    private lateinit var mAuth: FirebaseAuth
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initialize()

        splashScreens.alpha = 0.001f
        splashScreens.animate().setDuration(950).alpha(1f).withEndAction {

            if (mCurrentUser == null) {
                startActivity(Intent(this, LogIn::class.java))
                overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in)
                finish()
            } else {
                startActivity(Intent(this, SavedNotesActivity::class.java))
                overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in)
                finish()
            }
        }

    }

    private fun initialize() {
        splashScreens = findViewById<View>(R.id.splashScreen2) as ImageView

        mAuth = Firebase.auth
        mCurrentUser = mAuth.currentUser
    }
}