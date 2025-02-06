package com.faa.cmsportalcui.AdminFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.AdminAdapter.StaffAdapter
import com.faa.cmsportalcui.AdminModel.Staff
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore

class StaffFragment : Fragment() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var staffAdapter: StaffAdapter
    private val staffList = mutableListOf<Staff>()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var progressBar: ProgressBar


    private var id: String? = null
    private var description: String? = null
    private var commentText: String? = null
    private var photoUrl: String? = null
    private var profileImageUrl: String? = null
    private var timestamp: String? = null
    private var userId: String? = null
    private var adminId: String? = null
    private lateinit var etSearch: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_staff, container, false)

        rvUsers = binding.findViewById(R.id.rvUsers)
        progressBar = binding.findViewById(R.id.progressBar)
        etSearch = binding.findViewById(R.id.etSearch)  // Reference to the search EditText


        arguments?.let {
            id = it.getString("id")
            description = it.getString("title")
            commentText = it.getString("description")
            photoUrl = it.getString("photoUrl")
            profileImageUrl = it.getString("profileImageUrl")
            timestamp = it.getString("timestamp")
            userId = it.getString("userId")
            adminId = it.getString("adminId")
        }

        rvUsers.layoutManager = LinearLayoutManager(context)
        staffAdapter = StaffAdapter(
            requireContext(),
            staffList,
            id,
            commentText,
            description,
            photoUrl,
            profileImageUrl,
            timestamp,
            progressBar,
            userId,
            adminId
        )

        rvUsers.adapter = staffAdapter

        loadStaff()


        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                staffAdapter.filter.filter(s.toString()) // Use getFilter().filter()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        return binding
    }

    private fun loadStaff() {
        firestore.collection("staff")
            .get()
            .addOnSuccessListener { documents ->
                staffList.clear()
                for (document in documents) {
                    val staff = document.toObject(Staff::class.java)
                    staff.id = document.id
                    staffList.add(staff)
                }
                staffAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("StaffActivity", "Error getting documents", e)
            }
    }



    companion object {
        fun newInstance(
            id: String?,
            description: String?,
            commentText: String?,
            photoUrl: String?,
            profileImageUrl: String?,
            timestamp: String?,
            userId: String?,
            adminId: String?
        ): StaffFragment {
            val fragment = StaffFragment()
            val bundle = Bundle()
            bundle.putString("id", id)
            bundle.putString("title", description)
            bundle.putString("description", commentText)
            bundle.putString("photoUrl", photoUrl)
            bundle.putString("profileImageUrl", profileImageUrl)
            bundle.putString("timestamp", timestamp)
            bundle.putString("userId", userId)
            bundle.putString("adminId", adminId)
            fragment.arguments = bundle
            return fragment
        }
    }

}
