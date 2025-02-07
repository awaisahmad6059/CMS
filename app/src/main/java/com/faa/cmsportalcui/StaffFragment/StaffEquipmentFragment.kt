package com.faa.cmsportalcui.StaffFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.faa.cmsportalcui.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StaffEquipmentFragment : Fragment() {
    private var staffId: String? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        staffId = arguments?.getString("staff_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_staff_equipment, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        backBtn = view.findViewById(R.id.back_button)

        backBtn.setOnClickListener { requireActivity().onBackPressed() }

        val adapter = ViewPagerAdapter(this, staffId)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Approved"
                1 -> tab.text = "Pending"
                2 -> tab.text = "Rejected"
            }
        }.attach()

        return view
    }

    private class ViewPagerAdapter(fragment: Fragment, private val staffId: String?) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StaffApprovedFragment.newInstance(staffId)
                1 -> StaffPandingFragment.newInstance(staffId)
                else -> StaffRejectedFragment.newInstance(staffId)
            }
        }
    }
}
