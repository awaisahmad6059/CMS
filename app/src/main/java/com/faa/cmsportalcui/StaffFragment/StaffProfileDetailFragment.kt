package com.faa.cmsportalcui.StaffFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.faa.cmsportalcui.Authentication.LoginActivity
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.StaffSide.StaffEditProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class StaffProfileDetailFragment : Fragment() {
    private var staffId: String? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var logoutBtn: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val view = inflater.inflate(R.layout.fragment_staff_profile_detail, container, false)

        staffId = arguments?.getString("staff_id")

        logoutBtn = view.findViewById(R.id.logoutButton)

        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val workerName = view.findViewById<TextView>(R.id.workerName)
        val workerPosition = view.findViewById<TextView>(R.id.workerPosition)
        val specificationText = view.findViewById<TextView>(R.id.specificationText)
        val emailText = view.findViewById<TextView>(R.id.emailText)
        val experienceText = view.findViewById<TextView>(R.id.experienceText)
        val phoneText = view.findViewById<TextView>(R.id.phoneText)
        val editButton = view.findViewById<Button>(R.id.editButton)



        editButton.setOnClickListener {
            val intent = Intent(activity, StaffEditProfileActivity::class.java)
            intent.putExtra("staffId", staffId)
            startActivity(intent)
        }

        logoutBtn.setOnClickListener {
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        staffId?.let {
            db.collection("staff").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val staffData = document.data
                        staffData?.let { data ->
                            val profileImageUrl = data["profileImageUrl"] as? String
                            val name = data["name"] as? String
                            val position = data["position"] as? String
                            val specification = data["specification"] as? String
                            val experience = data["experience"] as? String
                            val email = data["email"] as? String
                            val phone = data["phone"] as? String

                            view?.findViewById<TextView>(R.id.workerName)?.text = name ?: "N/A"
                            view?.findViewById<TextView>(R.id.workerPosition)?.text = position ?: "N/A"
                            view?.findViewById<TextView>(R.id.specificationText)?.text = specification ?: "N/A"
                            view?.findViewById<TextView>(R.id.emailText)?.text = email ?: "N/A"
                            view?.findViewById<TextView>(R.id.experienceText)?.text = experience ?: "N/A"
                            view?.findViewById<TextView>(R.id.phoneText)?.text = phone ?: "N/A"

                            val profileImage = view?.findViewById<ImageView>(R.id.profileImage)
                            profileImageUrl?.let { url ->
                                Picasso.get().invalidate(url)
                                Picasso.get().load(url)
                                    .error(R.drawable.account)
                                    .into(profileImage)
                            } ?: profileImage?.setImageResource(R.drawable.account)
                        }
                    } else {
                        Toast.makeText(activity, "Staff not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error fetching staff details", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(activity, "Invalid staff ID", Toast.LENGTH_SHORT).show()
    }
}
