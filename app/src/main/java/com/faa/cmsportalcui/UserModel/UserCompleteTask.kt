package com.faa.cmsportalcui.UserModel


data class UserCompleteTask(
    var taskId: String = "", // Add taskId field
    val title: String = "",
    val currentDate: String = "",
    val userId: String = ""
)