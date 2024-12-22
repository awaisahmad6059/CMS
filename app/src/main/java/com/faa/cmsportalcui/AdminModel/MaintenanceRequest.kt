package com.faa.cmsportalcui.AdminModel

data class MaintenanceRequest(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String = "",
    val profileImageUrl: String,
    val imageUrl: String,
    val authorName: String,
    val commentText: String,
    val userType: String,
    val adminId: String? = null,
    val userId: String? = null
)
