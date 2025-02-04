package com.faa.cmsportalcui.AdminModel


data class PauseTask(
    val id: String = "",
    val title: String = "",
    val currentTime: String = "",
    val description: String = "",
    val location: String = "",
    val photoUrl: String = "",
    val roomNumber: String = "",
    val staffId: String = "",
    val timestamp: String = "",
    val userId: String = "",
    val userType: String = "",
    val assignedBy: String = "", // New field
    val assignedTaskId: String = "", // New field
    val comment: String = "", // New field
    val currentDate: String = "",
    val profileImageUrl: String = ""  // New field
)