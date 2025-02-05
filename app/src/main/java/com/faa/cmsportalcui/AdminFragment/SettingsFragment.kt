package com.faa.cmsportalcui.AdminFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.AdminSide.AdminDashboardActivity
import com.faa.cmsportalcui.AdminSide.AdminGeneralSettingActivity
import com.faa.cmsportalcui.AdminSide.AdminSecuritySettingActivity
import com.faa.cmsportalcui.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_setting, container, false)

        val generalSettingLayout: LinearLayout = view.findViewById(R.id.admin_general_setting)
        val securitySettingLayout: LinearLayout = view.findViewById(R.id.admin_security_setting)



        generalSettingLayout.setOnClickListener {
            val intent = Intent(requireActivity(), AdminGeneralSettingActivity::class.java)
            startActivity(intent)
        }

        securitySettingLayout.setOnClickListener {
            val intent = Intent(requireActivity(), AdminSecuritySettingActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
