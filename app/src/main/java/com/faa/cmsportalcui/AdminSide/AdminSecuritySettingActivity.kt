package com.faa.cmsportalcui.AdminSide

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.faa.cmsportalcui.R

class AdminSecuritySettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_security_setting)

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        val changePasswordLayout: LinearLayout = findViewById(R.id.linearLayout4)
        changePasswordLayout.setOnClickListener {
            val intent = Intent(this, AdminChangePasswordActivity::class.java)
            startActivity(intent)
        }}
}