package com.bankprotection.authentication.domain.repositories

interface ILocalAuthenticationDataSource {
    suspend fun getAuthenticationToken(): String?
    suspend fun setAuthenticationToken(token: String?)
    suspend fun getRefreshAuthenticationToken(): String?
    suspend fun setRefreshAuthenticationToken(refresh: String?)

    suspend fun clearAuthenticationToken()
    suspend fun clearRefreshAuthenticationToken()
    suspend fun clearTokens()
}