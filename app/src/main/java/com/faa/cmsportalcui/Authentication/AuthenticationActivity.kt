package com.faa.cmsportalcui.Authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.faa.cmsportalcui.R

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var loginBtn: Button
    private lateinit var SignupBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        // Initialize views
        backButton = findViewById(R.id.back_button)
        loginBtn = findViewById(R.id.login_button)
        SignupBtn = findViewById(R.id.signup_button)

        // Set up back button listener
        backButton.setOnClickListener {
            startActivity(Intent(this@AuthenticationActivity, WelcomeActivity::class.java))
            finishAffinity()
        }

        // Set up login button listener
        loginBtn.setOnClickListener {
            startActivity(Intent(this@AuthenticationActivity, LoginActivity::class.java))
        }

        // Set up signup button listener
        SignupBtn.setOnClickListener {
            startActivity(Intent(this@AuthenticationActivity, SignUpActivity::class.java))
        }
    }
}
