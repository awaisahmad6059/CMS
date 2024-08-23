package com.faa.cmsportalcui.StaffModel

data class CompletedTask(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val roomNumber: String = "",
    val assignedBy: String = "",
    val location: String = "",
    val photoUrl: String = "",
    val timestamp: String = "",
    val staffId: String = "",
    val userId: String? = null,
    val adminId: String? = null,
    val currentDate: String = "",
    val currentTime: String = "",
    val userType: String = ""
)
