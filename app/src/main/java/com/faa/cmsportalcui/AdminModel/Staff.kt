package com.faa.cmsportalcui.AdminModel


data class Staff(
    var id: String = "",
    val name: String = "",
    val jobTitle: String = "",
    val location: String = "",
    val email: String = "",
    val specification: String = "",
    val position: String = "",
    val experience: String = "",
    val phone: String = "",
    var profileImageUrl: String? = null,
    var availability: Map<String, String> = mapOf()
)
