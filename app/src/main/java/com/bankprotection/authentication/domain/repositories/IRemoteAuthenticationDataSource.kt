package com.bankprotection.authentication.domain.repositories

import com.bankprotection.authentication.domain.models.AuthenticationTokenResponse
import com.bankprotection.authentication.domain.models.RefreshAuthenticationTokenResponse
import okhttp3.RequestBody
import retrofit2.Response

interface IRemoteAuthenticationDataSource {
    suspend fun getAuthenticationToken(
        requestBody: RequestBody
    ): Response<AuthenticationTokenResponse>

    suspend fun refreshAuthenticationToken(
        requestBody: RequestBody
    ): Response<RefreshAuthenticationTokenResponse>
}