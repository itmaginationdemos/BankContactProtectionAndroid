package com.bankprotection.authentication.data

import com.bankprotection.authentication.domain.repositories.ILocalAuthenticationDataSource
import com.bankprotection.common.domain.repository.IPreferencesDataSource
import javax.inject.Inject

class LocalAuthenticationDataSource @Inject constructor(
    private val preferencesDataSource: IPreferencesDataSource
) : ILocalAuthenticationDataSource {
    override suspend fun getAuthenticationToken(): String? {
        return preferencesDataSource.getNotEmptyStringOrNull(AUTHENTICATION_TOKEN_KEY)
    }

    override suspend fun setAuthenticationToken(token: String?) {
        preferencesDataSource.putString(AUTHENTICATION_TOKEN_KEY, token)
    }

    override suspend fun getRefreshAuthenticationToken(): String? {
        return preferencesDataSource.getNotEmptyStringOrNull(REFRESH_AUTHENTICATION_TOKEN_KEY)
    }

    override suspend fun setRefreshAuthenticationToken(refresh: String?) {
        preferencesDataSource.putString(REFRESH_AUTHENTICATION_TOKEN_KEY, refresh)
    }

    override suspend fun clearAuthenticationToken() {
        preferencesDataSource.remove(AUTHENTICATION_TOKEN_KEY)
    }

    override suspend fun clearRefreshAuthenticationToken() {
        preferencesDataSource.remove(REFRESH_AUTHENTICATION_TOKEN_KEY)
    }

    override suspend fun clearTokens() {
        clearAuthenticationToken()
        clearRefreshAuthenticationToken()
    }

    companion object {
        private const val AUTHENTICATION_TOKEN_KEY =
            "com.bankprotection.authentication.data.AUTHENTICATION_TOKEN_KEY"
        private const val REFRESH_AUTHENTICATION_TOKEN_KEY =
            "com.bankprotection.authentication.data.REFRESH_AUTHENTICATION_TOKEN_KEY"
    }
}