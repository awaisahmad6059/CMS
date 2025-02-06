package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.AdminFragment.AdminDashboardFragment
import com.faa.cmsportalcui.AdminFragment.SettingsFragment
import com.faa.cmsportalcui.AdminFragment.StaffFragment
import com.faa.cmsportalcui.AdminFragment.UserManagementFragment
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffFragment.StaffDashboardFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        loadFragment(AdminDashboardFragment(), false)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(AdminDashboardFragment(), true)
                R.id.user -> loadFragment(UserManagementFragment(), true)
                R.id.staff -> loadFragment(StaffFragment(), true)
                R.id.settings -> loadFragment(SettingsFragment(), true)
            }
            true
        }
    }


    private fun loadFragment(fragment: Fragment, withAnimation: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()

        if (withAnimation) {
            transaction.setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_down
            )
        }

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is AdminDashboardFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
