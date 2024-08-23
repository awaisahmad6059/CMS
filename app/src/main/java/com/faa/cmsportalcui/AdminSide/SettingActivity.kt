package com.faa.cmsportalcui.AdminSide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R

class SettingActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val backButton: ImageButton = findViewById(R.id.back_button)
        val generalSettingLayout: LinearLayout = findViewById(R.id.admin_general_setting)
        val securitySettingLayout: LinearLayout = findViewById(R.id.admin_security_setting)

        backButton.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        generalSettingLayout.setOnClickListener {
            val intent = Intent(this, AdminGeneralSettingActivity::class.java)
            startActivity(intent)
        }

        securitySettingLayout.setOnClickListener {
            val intent = Intent(this, AdminSecuritySettingActivity::class.java)
            startActivity(intent)
        }
    }
}
