package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserFragment.UserDashboardFragment
import com.faa.cmsportalcui.UserFragment.UserMaintenanceRequestFragment
import com.faa.cmsportalcui.UserFragment.UserProfileFragment
import com.faa.cmsportalcui.UserFragment.UserSettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Retrieve the user ID from the intent
        userId = intent.getStringExtra("user_id")

        // Default fragment load
        if (savedInstanceState == null) {
            loadFragment(UserDashboardFragment(), userId)
        }

        // Bottom navigation item selection listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(UserDashboardFragment(), userId)
                R.id.activities -> loadFragment(UserMaintenanceRequestFragment(), userId)
                R.id.profile -> loadFragment(UserProfileFragment(), userId)
                R.id.setting -> loadFragment(UserSettingFragment(), userId)
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment, userId: String?) {
        val bundle = Bundle().apply {
            putString("user_id", userId)
        }
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
