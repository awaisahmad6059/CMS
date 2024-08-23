package com.faa.cmsportalcui.AdminModel


data class Admin(
    val name: String = "",
    val userType: String = "",
    val password: String = "" // Note: Storing plain passwords is not recommended
)