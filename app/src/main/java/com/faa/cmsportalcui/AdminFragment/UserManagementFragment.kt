package com.faa.cmsportalcui.AdminFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.faa.cmsportalcui.UserAdapter.User
import com.faa.cmsportalcui.UserAdapter.UserAdapter
import com.faa.cmsportalcui.databinding.FragmentUserManagementBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserManagementFragment : Fragment() {

    private lateinit var binding: FragmentUserManagementBinding
    private lateinit var userAdapter: UserAdapter
    private val users = mutableListOf<User>()
    private val allUsers = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUsers.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(users)
        binding.rvUsers.adapter = userAdapter


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
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle the error
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
