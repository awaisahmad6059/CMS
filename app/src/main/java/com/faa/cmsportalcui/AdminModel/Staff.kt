package com.faa.cmsportalcui.AdminModel


data class Staff(
    var id: String = "", // Ensure id is mutable
    val name: String = "",
    val jobTitle: String = "",
    val location: String = "",
    val email: String = "",
    val phone: String = "",
    var profileImageUrl: String? = null,
    var availability: Map<String, String> = mapOf()
)
