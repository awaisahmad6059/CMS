package com.faa.cmsportalcui.StaffFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.faa.cmsportalcui.AdminModel.AdminEquipmentRequest
import com.faa.cmsportalcui.StaffAdapter.EquipmentRequestAdapter
import com.faa.cmsportalcui.databinding.FragmentStaffPandingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StaffPandingFragment : Fragment() {

    private lateinit var binding: FragmentStaffPandingBinding
    private lateinit var adapter: EquipmentRequestAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var staffId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            staffId = it.getString("staff_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffPandingBinding.inflate(inflater, container, false)

        setupRecyclerView()
        fetchEquipmentRequests()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = EquipmentRequestAdapter(mutableListOf())
        binding.recylerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recylerview.adapter = adapter
    }

    private fun fetchEquipmentRequests() {
        val loggedInStaffId = auth.currentUser?.uid ?: return
        if (staffId == null) return

        firestore.collection("equipmentsrequest")
            .whereEqualTo("staffId", staffId)
            .whereEqualTo("status", "Pending")
            .orderBy("requestTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val requests = value?.documents?.mapNotNull { it.toObject(AdminEquipmentRequest::class.java) } ?: emptyList()
                adapter.updateList(requests)
            }
    }



    companion object {
        fun newInstance(staffId: String?) = StaffPandingFragment().apply {
            arguments = Bundle().apply {
                putString("staff_id", staffId)
            }
        }
    }
}
