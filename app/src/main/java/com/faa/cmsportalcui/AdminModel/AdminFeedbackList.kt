package com.faa.cmsportalcui.AdminModel

data class AdminFeedbackList(
    val id: String = "",
    val assignedTaskId: String = "",
    val title: String = "",
    val review: String? = null, // Optional field to check if the task is completed and has a review
    val staffId: String = "", // Add staffId to reference the staff collection
    val assignedBy: String = "" // Add staffId to reference the staff collection
)
