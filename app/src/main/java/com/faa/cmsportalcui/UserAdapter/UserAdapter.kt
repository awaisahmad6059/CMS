package com.faa.cmsportalcui.UserAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faa.cmsportalcui.databinding.UsermanagementItemBinding
import com.squareup.picasso.Picasso
data class User(
    val username: String = "",
    val userType: String = "",
    val profileImageUrl: String = ""
)

class UserAdapter(private val users: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: UsermanagementItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UsermanagementItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvName.text = user.username
        holder.binding.tvRole.text = user.userType
        Picasso.get().load(user.profileImageUrl).into(holder.binding.imgProfile)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}



