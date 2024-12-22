package com.faa.cmsportalcui.UserSide


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserAdapter.UserDashboardRequestAdapter
import com.faa.cmsportalcui.UserModel.UserDashboardRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var userName: TextView
    private lateinit var userDesc: TextView
    private lateinit var userProfileImage: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var requestButton: ImageView
    private lateinit var notificationButton: ImageView
    private lateinit var completeTaskButton: ImageView
    private lateinit var requestMaintenanceLayout: LinearLayout
    private lateinit var notificationLayout: LinearLayout
    private lateinit var completedTaskLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var userDashboardRequestAdapter: UserDashboardRequestAdapter
    private var userId: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        userName = findViewById(R.id.user_name)
        userDesc = findViewById(R.id.user_desc)
        userProfileImage = findViewById(R.id.profile)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        requestButton = findViewById(R.id.request)
        notificationButton = findViewById(R.id.notification_arrow)
        completeTaskButton = findViewById(R.id.completetask)
        requestMaintenanceLayout = findViewById(R.id.requestmaintanace)
        notificationLayout = findViewById(R.id.notification)
        completedTaskLayout = findViewById(R.id.completedtask)
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)

        userId = intent.getStringExtra("user_id")

        if (userId != null) {
            loadUserProfile(userId!!)
            loadUserRequests(userId!!)
        } else {
            Log.e("UserDashboardActivity", "User ID is null")
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.activities -> {
                    startActivity(Intent(this@UserDashboardActivity, UserMaintanancerequestActivity::class.java).apply {
                        putExtra("user_id", userId)
                    })
                    true
                }
                R.id.setting -> {
                    startActivity(Intent(this@UserDashboardActivity, UserSettingActivity::class.java).apply {
                        putExtra("user_id", userId)
                    })
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this@UserDashboardActivity, UserProfileActivity::class.java).apply {
                        putExtra("user_id", userId)
                    })
                    true
                }
                else -> false
            }
        }

        requestButton.setOnClickListener {
            startActivity(Intent(this@UserDashboardActivity, UserRequestActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        notificationButton.setOnClickListener {
            startActivity(Intent(this@UserDashboardActivity, UserNotificationActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        completeTaskButton.setOnClickListener {
            startActivity(Intent(this@UserDashboardActivity, UserCompleteFeddbackActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        requestMaintenanceLayout.setOnClickListener {
            startActivity(Intent(this@UserDashboardActivity, UserRequestActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        notificationLayout.setOnClickListener {
            startActivity(Intent(this@UserDashboardActivity, UserNotificationActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }

        completedTaskLayout.setOnClickListener {
            startActivity(Intent(this@UserDashboardActivity, UserCompleteFeddbackActivity::class.java).apply {
                putExtra("user_id", userId)
            })
        }
    }

    private fun loadUserProfile(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("username")
                    val desc = document.getString("description")
                    val profileImageUrl = document.getString("profileImageUrl")

                    userName.text = name
                    userDesc.text = desc
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
                    Log.d("UserDashboardActivity", "No requests found for user")
                } else {
                    userDashboardRequestAdapter = UserDashboardRequestAdapter(requests)
                    recyclerView.adapter = userDashboardRequestAdapter
                    Log.d("UserDashboardActivity", "Adapter set with ${requests.size} requests")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserDashboardActivity", "Error fetching user requests", e)
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
