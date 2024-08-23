package com.faa.cmsportalcui.StaffModel


data class AssignedMe(
    val id: String = "",
    var assignedTaskId: String = "", // New field
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val photoUrl: String = "",
    val roomNumber: String = "",
    val status: String = "",
    val timestamp: String = "",
    val userId: String = "",
    val adminId: String = "",
    val userType: String = ""
)
