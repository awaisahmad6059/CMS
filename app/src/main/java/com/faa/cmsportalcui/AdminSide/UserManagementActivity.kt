package com.faa.cmsportalcui.AdminSide
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.faa.cmsportalcui.UserAdapter.User
import com.faa.cmsportalcui.UserAdapter.UserAdapter
import com.faa.cmsportalcui.databinding.ActivityUserManagementBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserManagementBinding
    private lateinit var userAdapter: UserAdapter
    private val users = mutableListOf<User>()
    private val allUsers = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(users)
        binding.rvUsers.adapter = userAdapter

        val backButton: ImageView = binding.backButton
        backButton.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }
// Inside UserManagementActivity onCreate method
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()  // Access the input text
                filterUsers(query)  // Call filter function with the query
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        fetchUsers()
    }

    private fun fetchUsers() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").get()
            .addOnSuccessListener { result: QuerySnapshot ->
                users.clear()
                for (document in result) {
                    val username = document.getString("username") ?: ""
                    val userType = document.getString("userType") ?: ""
                    val profileImageUrl = document.getString("profileImageUrl") ?: "https://firebasestorage.googleapis.com/v0/b/cms-portal-cui.appspot.com/o/placeholder.png?alt=media&token=3d835e45-cb27-44bf-a024-c922c7cbc9a6"
                    val user = User(username, userType, profileImageUrl)
                    allUsers.add(user)
                    users.add(User(username, userType, profileImageUrl))
                }
                users.clear()
                users.addAll(allUsers)
                userAdapter.notifyDataSetChanged()            }
            .addOnFailureListener { exception ->
            }
    }
    private fun filterUsers(query: String) {
        if (query.isEmpty()) {
            users.clear()
            users.addAll(allUsers)
        } else {
            val filteredList = allUsers.filter {
                it.username.contains(query, ignoreCase = true)
            }
            users.clear()
            users.addAll(filteredList)
        }
        userAdapter.notifyDataSetChanged()
    }
}
