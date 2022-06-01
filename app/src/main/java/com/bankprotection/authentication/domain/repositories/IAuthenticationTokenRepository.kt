package com.bankprotection.authentication.domain.repositories

interface IAuthenticationTokenRepository {
    fun setAuthenticationToken(token: String?)
    fun getAuthenticationToken(): String?
    fun setRefreshToken(token: String?)
    fun getRefreshToken(): String?
}