package com.faa.cmsportalcui.AdminFragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.AdminEquipmentRequestAdapter
import com.faa.cmsportalcui.AdminModel.AdminEquipmentRequest
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ApproveFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var equipmentRequests: MutableList<AdminEquipmentRequest>
    private lateinit var adapter: AdminEquipmentRequestAdapter
    private lateinit var firestore: FirebaseFirestore
    private var allRequests: MutableList<AdminEquipmentRequest> = mutableListOf()
    private var listenerRegistration: ListenerRegistration? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recylerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        equipmentRequests = mutableListOf()
        adapter = AdminEquipmentRequestAdapter(equipmentRequests)
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()

        fetchPendingRequests()
    }

    private fun fetchPendingRequests() {
        listenerRegistration = firestore.collection("equipmentsrequest")
            .whereEqualTo("status", "approved")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    println("Error fetching requests: $error")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    equipmentRequests.clear()
                    snapshots.documents.forEach { document ->
                        val equipmentRequest = document.toObject(AdminEquipmentRequest::class.java)
                        if (equipmentRequest != null) {
                            equipmentRequests.add(equipmentRequest)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove() // Unregister the real-time listener
    }
}
