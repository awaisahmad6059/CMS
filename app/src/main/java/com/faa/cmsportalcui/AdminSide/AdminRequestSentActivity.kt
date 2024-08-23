package com.faa.cmsportalcui.AdminSide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R

class AdminRequestSentActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var okButton: Button
        lateinit var backButton: ImageView

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_request_sent)

        okButton = findViewById(R.id.okButton)
        backButton = findViewById(R.id.back_button)
        okButton.setOnClickListener {
            startActivity(Intent(this@AdminRequestSentActivity, AdminDashboardActivity::class.java))
        }
        backButton.setOnClickListener {
            startActivity(Intent(this@AdminRequestSentActivity, AdminDashboardActivity::class.java))
        }


    }
}