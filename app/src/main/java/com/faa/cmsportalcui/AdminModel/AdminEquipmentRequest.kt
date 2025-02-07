package com.faa.cmsportalcui.AdminModel

data class AdminEquipmentRequest(
    val id: String = "",
    val assignedTaskId: String = "",
    val title: String = "",
    val description: String = "",
    val roomNumber: String = "",
    val assignedBy: String = "",
    val location: String = "",
    val photoUrl: String = "",
    val timestamp: String = "",
    val comment: String = "",
    val currentDate: String = "",
    val currentTime: String = "",
    val staffId: String = "",
    val equipmentName: String = "",
    val reason: String = "",
    val requestTimestamp: String = "",
    val status: String = "Pending"
)
