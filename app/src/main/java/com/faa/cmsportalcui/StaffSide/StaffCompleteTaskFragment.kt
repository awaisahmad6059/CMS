package com.faa.cmsportalcui.StaffSide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffAdapter.CompletedTaskAdapter
import com.faa.cmsportalcui.StaffModel.CompletedTask
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class StaffCompleteTaskFragment : Fragment() {

    private var staffId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var addTask: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var completedTaskAdapter: CompletedTaskAdapter
    private var tasksListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_staff_complete_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        staffId = arguments?.getString("staff_id")

        recyclerView = view.findViewById(R.id.recyclerViewCompletedTasks)
        addTask = view.findViewById(R.id.addCompletedTaskButton)

        firestore = FirebaseFirestore.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        completedTaskAdapter = CompletedTaskAdapter(emptyList())
        recyclerView.adapter = completedTaskAdapter

        setupRealTimeTaskUpdates()

        addTask.setOnClickListener {
            val intent = Intent(requireActivity(), StaffAssignedMeActivity::class.java).apply {
                putExtra("staffId", staffId)
            }
            startActivity(intent)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        tasksListener?.remove()
    }

    private fun setupRealTimeTaskUpdates() {
        staffId?.let {
            tasksListener = firestore.collection("completeTask")
                .whereEqualTo("staffId", it)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("StaffCompleteTaskFragment", "Error fetching tasks", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val tasks = snapshot.documents.mapNotNull { document ->
                            document.toObject(CompletedTask::class.java)
                        }
                        completedTaskAdapter = CompletedTaskAdapter(tasks)
                        recyclerView.adapter = completedTaskAdapter
                    }
                }
        }
    }
}
