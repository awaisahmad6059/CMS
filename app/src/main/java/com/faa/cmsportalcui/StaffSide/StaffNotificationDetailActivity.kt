package com.faa.cmsportalcui.StaffSide

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R

class StaffNotificationDetailActivity : AppCompatActivity() {
    private lateinit var titleBackground: TextView
    private lateinit var detailsBackground: TextView
    private lateinit var closeButton: Button
    private lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_notification_detail)

        titleBackground = findViewById(R.id.titleBackground)
        detailsBackground = findViewById(R.id.detailsBackground)
        closeButton = findViewById(R.id.closeButton)
        backBtn = findViewById(R.id.back_button)

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")

        titleBackground.text = title ?: "No Title"
        detailsBackground.text = description ?: "No Description"

        closeButton.setOnClickListener {
            finish()
        }
        backBtn .setOnClickListener {
            finish()
        }
    }
}
