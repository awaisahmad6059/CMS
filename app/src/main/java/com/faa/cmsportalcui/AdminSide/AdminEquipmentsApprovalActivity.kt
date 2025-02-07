package com.faa.cmsportalcui.AdminSide

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.faa.cmsportalcui.AdminFragment.ApproveFragment
import com.faa.cmsportalcui.AdminFragment.PendingFragment
import com.faa.cmsportalcui.AdminFragment.RejectFragment
import com.faa.cmsportalcui.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdminEquipmentsApprovalActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_equipments_approval)

        backBtn = findViewById(R.id.back_button)
        backBtn.setOnClickListener {
            finish()
        }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Pending"
                1 -> tab.text = "Approve"
                2 -> tab.text = "Reject"
            }
        }.attach()
    }

    private class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PendingFragment()
                1 -> ApproveFragment()
                else -> RejectFragment()
            }
        }
    }
}
