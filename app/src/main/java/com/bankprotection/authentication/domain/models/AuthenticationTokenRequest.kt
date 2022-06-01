package com.bankprotection.authentication.domain.models

data class AuthenticationTokenRequest(
    val username: String,
    val password: String
)