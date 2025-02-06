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
            loadFragment(StaffDashboardFragment(), staffId, false)
        }
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(StaffDashboardFragment(), staffId, true)
                R.id.complete -> loadFragment(StaffCompleteTaskFragment(), staffId, true)
                R.id.profile -> loadFragment(StaffProfileDetailFragment(), staffId, true)
            }
            true
        }


    }
    private fun loadFragment(fragment: Fragment, staffId: String?, withAnimation: Boolean) {
        val bundle = Bundle().apply {
            putString("staff_id", staffId)
        }
        fragment.arguments = bundle

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
        // Check if the current fragment is the StaffDashboardFragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is StaffDashboardFragment) {
            // If the current fragment is StaffDashboardFragment, close the activity instead of navigating back
            finish()  // Close the activity
        } else {
            // If it's not the home fragment, follow the default back navigation behavior
            super.onBackPressed()
        }
    }


}
