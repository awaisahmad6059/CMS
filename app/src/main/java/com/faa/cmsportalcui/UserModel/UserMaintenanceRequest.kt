package com.faa.cmsportalcui.UserModel

data class UserMaintenanceRequest(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val room: String = "",
    val photoUrl: String = "",
    val date: String = ""
)