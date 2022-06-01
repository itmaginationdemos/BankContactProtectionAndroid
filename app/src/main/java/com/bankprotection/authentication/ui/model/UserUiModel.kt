package com.bankprotection.authentication.ui.model

import com.bankprotection.authentication.domain.models.UserDetailsResponse

data class UserUiModel(
    val username: String?,
    val firstName: String?,
    val email: String?,
    val isActive: Boolean,
    val isStaff: Boolean,
    val phoneNumber: String?
)

fun UserDetailsResponse.mapToUiModel(): UserUiModel =
    UserUiModel(
        username = this.username,
        firstName = this.first_name,
        email = this.email,
        isActive = this.is_active,
        isStaff = this.is_staff,
        phoneNumber = this.phone_number
    )
