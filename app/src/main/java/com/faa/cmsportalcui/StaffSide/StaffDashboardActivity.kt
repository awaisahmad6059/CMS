package com.faa.cmsportalcui.StaffSide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffFragment.StaffCompleteTaskFragment
import com.faa.cmsportalcui.StaffFragment.StaffDashboardFragment
import com.faa.cmsportalcui.StaffFragment.StaffProfileDetailFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class StaffDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var staffId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard)
        bottomNavigationView = findViewById(R.id.bottom_navigation)


        staffId = intent.getStringExtra("staff_id")
        if (savedInstanceState == null) {
            loadFragment(StaffDashboardFragment(), staffId)
        }
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(StaffDashboardFragment(), staffId)
                R.id.complete -> loadFragment(StaffCompleteTaskFragment(), staffId)
                R.id.profile -> loadFragment(StaffProfileDetailFragment(), staffId)
            }
            true
        }


    }
    private fun loadFragment(fragment: Fragment, staffId: String?) {
        val bundle = Bundle().apply {
            putString("staff_id", staffId)
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
