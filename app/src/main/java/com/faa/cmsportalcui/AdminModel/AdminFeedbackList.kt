package com.faa.cmsportalcui.AdminModel

data class AdminFeedbackList(
    val id: String = "",
    val assignedTaskId: String = "",
    val title: String = "",
    val rating: Float = 0f,
    val review: String? = null,
    val staffId: String = "",
    val assignedBy: String = ""
)
