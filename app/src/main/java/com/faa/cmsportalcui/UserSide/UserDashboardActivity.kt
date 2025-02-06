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

        userId = intent.getStringExtra("user_id")

        if (savedInstanceState == null) {
            loadFragment(UserDashboardFragment(), userId, false)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(UserDashboardFragment(), userId, true)
                R.id.activities -> loadFragment(UserMaintenanceRequestFragment(), userId, true)
                R.id.profile -> loadFragment(UserProfileFragment(), userId, true)
                R.id.setting -> loadFragment(UserSettingFragment(), userId, true)
            }
            true
        }
    }


    private fun loadFragment(fragment: Fragment, userId: String?, withAnimation: Boolean) {
        val bundle = Bundle().apply {
            putString("user_id", userId)
        }
        fragment.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()

        if (withAnimation) {
            transaction.setCustomAnimations(
                R.anim.slide_in_up,   // Slide in from bottom
                R.anim.fade_out,      // Fade out (current fragment)
                R.anim.fade_in,       // Fade in (new fragment)
                R.anim.slide_out_down // Slide out to bottom
            )
        }

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}
