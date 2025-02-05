package com.faa.cmsportalcui.UserFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserAdapter.UserDashboardRequestAdapter
import com.faa.cmsportalcui.UserModel.UserDashboardRequest
import com.faa.cmsportalcui.UserSide.UserCompleteFeddbackActivity
import com.faa.cmsportalcui.UserSide.UserMaintanancerequestActivity
import com.faa.cmsportalcui.UserSide.UserNotificationActivity
import com.faa.cmsportalcui.UserSide.UserRequestActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserDashboardFragment : Fragment() {

    private lateinit var userName: TextView
    private lateinit var userProfileImage: ImageView
    private lateinit var requestMaintenanceLayout: LinearLayout
    private lateinit var notificationLayout: LinearLayout
    private lateinit var completedTaskLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var userDashboardRequestAdapter: UserDashboardRequestAdapter
    private var userId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val view = inflater.inflate(R.layout.fragment_user_dashboard, container, false)

        // Initialize views
        userName = view.findViewById(R.id.user_name)
        userProfileImage = view.findViewById(R.id.profile)
        requestMaintenanceLayout = view.findViewById(R.id.requestmaintanace)
        notificationLayout = view.findViewById(R.id.notification)
        completedTaskLayout = view.findViewById(R.id.completedtask)
        recyclerView = view.findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Retrieve the user ID from the fragment arguments
        userId = arguments?.getString("user_id")

        if (userId != null) {
            loadUserProfile(userId!!)
            loadUserRequests(userId!!)
        } else {
            Log.e("UserDashboardFragment", "User ID is null")
        }

        requestMaintenanceLayout.setOnClickListener {
            val intent = Intent(requireContext(), UserRequestActivity::class.java)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }


        notificationLayout.setOnClickListener {
            val intent = Intent(requireContext(), UserNotificationActivity::class.java)
            intent.putExtra("user_id", userId)

            startActivity(intent)        }

        completedTaskLayout.setOnClickListener {
            val intent = Intent(requireContext(), UserCompleteFeddbackActivity::class.java)
            intent.putExtra("user_id", userId)

            startActivity(intent)        }

        return view
    }

    private fun loadUserProfile(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("username")
//                    val desc = document.getString("description")
                    val profileImageUrl = document.getString("profileImageUrl")

                    userName.text = name
//                    userDesc.text = desc
                    if (profileImageUrl != null) {
                        Picasso.get().load(profileImageUrl).into(userProfileImage)
                    }
                } else {
                    Log.d("UserDashboardActivity", "No user profile found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserDashboardActivity", "Error loading user profile", e)
            }
    }

    private fun loadUserRequests(userId: String) {
        firestore.collection("users").document(userId).collection("requests")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(2)
            .get()
            .addOnSuccessListener { documents ->
                val requests = mutableListOf<UserDashboardRequest>()
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val timestampString = document.getString("timestamp") ?: ""

                    requests.add(UserDashboardRequest(title, timestampString))
                }
                if (requests.isEmpty()) {
                    Log.d("UserDashboardFragment", "No requests found for user")
                } else {
                    userDashboardRequestAdapter = UserDashboardRequestAdapter(requests)
                    recyclerView.adapter = userDashboardRequestAdapter
                    Log.d("UserDashboardFragment", "Adapter set with ${requests.size} requests")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserDashboardFragment", "Error fetching user requests", e)
            }
    }

    companion object {
        // Factory method to create a new instance of the fragment with user_id as an argument
        @JvmStatic
        fun newInstance(userId: String) = UserDashboardFragment().apply {
            arguments = Bundle().apply {
                putString("user_id", userId)
            }
        }
    }
}
