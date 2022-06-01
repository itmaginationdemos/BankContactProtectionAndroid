package com.bankprotection.authentication.data

import com.bankprotection.authentication.domain.models.AuthenticationTokenResponse
import com.bankprotection.authentication.domain.models.RefreshAuthenticationTokenResponse
import com.bankprotection.authentication.domain.repositories.IRemoteAuthenticationDataSource
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RemoteAuthenticationDataSource : IRemoteAuthenticationDataSource {
    @POST("/api/auth/token/")
    override suspend fun getAuthenticationToken(
        @Body requestBody: RequestBody
    ): Response<AuthenticationTokenResponse>

    @POST("/api/auth/token/refresh/")
    override suspend fun refreshAuthenticationToken(
        @Body requestBody: RequestBody
    ): Response<RefreshAuthenticationTokenResponse>
}