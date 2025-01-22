package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R

class StaffPauseSentActivity : AppCompatActivity() {

    private var staffId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_pause_sent)

        // Retrieve staffId from the intent
        staffId = intent.getStringExtra("staff_id")

        // Show staffId in a Toast
        if (staffId != null) {
            Toast.makeText(this, "Staff ID: $staffId", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Staff ID is null", Toast.LENGTH_SHORT).show()
        }
    }
}