package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffAdapter.NotificationAdapter
import com.faa.cmsportalcui.StaffAdapter.TaskAdapter
import com.faa.cmsportalcui.StaffModel.Notification
import com.faa.cmsportalcui.StaffModel.Task
import com.faa.cmsportalcui.UserFragment.UserDashboardFragment
import com.faa.cmsportalcui.UserFragment.UserMaintenanceRequestFragment
import com.faa.cmsportalcui.UserFragment.UserProfileFragment
import com.faa.cmsportalcui.UserFragment.UserSettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


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
