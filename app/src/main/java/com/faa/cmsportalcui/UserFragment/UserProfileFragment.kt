package com.faa.cmsportalcui.UserFragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.R
import com.faa.cmsportalcui.UserSide.UserDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class UserProfileFragment : Fragment() {

    private var userId: String? = null

    private lateinit var saveBtn: Button
    private lateinit var editPhotoBtn: Button
    private lateinit var profilePhoto: ImageView
    private lateinit var fullName: EditText
    private lateinit var description: EditText
    private lateinit var phone: EditText
    private lateinit var progressBar: ProgressBar

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_profile, container, false)

        userId = arguments?.getString("user_id")

        saveBtn = rootView.findViewById(R.id.button_save)
        progressBar = rootView.findViewById(R.id.progressBar)
        editPhotoBtn = rootView.findViewById(R.id.button_edit_photo)
        profilePhoto = rootView.findViewById(R.id.profile_photo)
        fullName = rootView.findViewById(R.id.edit_full_name)
        description = rootView.findViewById(R.id.editdescription)
        phone = rootView.findViewById(R.id.edit_phone)

        editPhotoBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        saveBtn.setOnClickListener {
            saveUserProfile()
        }



        loadUserData()

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            profilePhoto.setImageURI(imageUri)
        }
    }

    private fun saveUserProfile() {
        val name = fullName.text.toString()
        val desc = description.text.toString()
        val phoneText = phone.text.toString()
        val userId = this.userId ?: return

        progressBar.visibility = View.VISIBLE

        if (imageUri != null) {
            val ref = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}")

            val compressedImage = compressImage(imageUri!!)

            ref.putBytes(compressedImage)
                .addOnSuccessListener { taskSnapshot ->
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        updateUserDetails(userId, name, desc, phoneText, uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Image Upload Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val currentImageUrl = document.getString("profileImageUrl")
                        updateUserDetails(userId, name, desc, phoneText, currentImageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun compressImage(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun updateUserDetails(userId: String, name: String, desc: String, phone: String, imageUrl: String?) {
        val userMap = hashMapOf(
            "fullName" to name,
            "username" to name,
            "description" to desc,
            "phone" to phone,
            "profileImageUrl" to imageUrl
        )

        firestore.collection("users").document(userId)
            .update(userMap as Map<String, Any>)
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), UserDashboardActivity::class.java)
                    intent.putExtra("user_id", userId)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    Toast.makeText(requireContext(), "Profile Update Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadUserData() {
        userId?.let {
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        fullName.setText(document.getString("fullName"))
                        description.setText(document.getString("description"))
                        phone.setText(document.getString("phone"))
                        val profileImageUrl = document.getString("profileImageUrl")
                        if (profileImageUrl != null) {
                            Glide.with(requireContext())
                                .load(profileImageUrl)
                                .into(profilePhoto)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


}
