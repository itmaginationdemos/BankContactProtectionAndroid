package com.bankprotection.authentication.domain.models

data class UserDetailsResponse(
    val id: Int,
    val username: String?,
    val first_name: String?,
    val email: String?,
    val is_active: Boolean,
    val is_staff: Boolean,
    val phone_number: String?
)