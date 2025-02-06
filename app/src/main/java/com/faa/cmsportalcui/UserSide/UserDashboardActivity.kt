package com.faa.cmsportalcui.UserSide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.AdminFragment.AdminDashboardFragment
import com.faa.cmsportalcui.AdminFragment.SettingsFragment
import com.faa.cmsportalcui.AdminFragment.StaffFragment
import com.faa.cmsportalcui.AdminFragment.UserManagementFragment
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffFragment.StaffDashboardFragment
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
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is UserMaintenanceRequestFragment || currentFragment is UserProfileFragment || currentFragment is UserSettingFragment) {
            loadFragment(UserDashboardFragment(),userId, false)
        } else if (currentFragment is UserDashboardFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
