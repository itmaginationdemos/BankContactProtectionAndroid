package com.bankprotection.authentication.domain.models

data class AuthenticationTokenResponse(
    val access: String,
    val refresh: String
)