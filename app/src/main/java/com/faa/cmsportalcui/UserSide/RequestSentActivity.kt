package com.faa.cmsportalcui.UserSide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R

class RequestSentActivity : AppCompatActivity() {

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_sent)

        // Retrieve userId from the intent
        userId = intent.getStringExtra("user_id")

        val okButton: Button = findViewById(R.id.okButton)
        val backButton: ImageView = findViewById(R.id.back_button)

        okButton.setOnClickListener {
            navigateToUserDashboard()
        }

        backButton.setOnClickListener {
            navigateToUserDashboard()
        }
    }

    private fun navigateToUserDashboard() {
        val intent = Intent(this, UserDashboardActivity::class.java).apply {
            putExtra("user_id", userId)
        }
        startActivity(intent)
        finish()
    }
}
