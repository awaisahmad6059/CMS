package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.AdminFragment.AdminDashboardFragment
import com.faa.cmsportalcui.AdminFragment.SettingsFragment
import com.faa.cmsportalcui.AdminFragment.StaffFragment
import com.faa.cmsportalcui.AdminFragment.UserManagementFragment
import com.faa.cmsportalcui.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        loadFragment(AdminDashboardFragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(AdminDashboardFragment())
                R.id.user -> loadFragment(UserManagementFragment())
                R.id.staff -> loadFragment(StaffFragment())
                R.id.settings -> loadFragment(SettingsFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
