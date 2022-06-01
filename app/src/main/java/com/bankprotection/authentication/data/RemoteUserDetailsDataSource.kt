package com.bankprotection.authentication.data

import com.bankprotection.authentication.domain.models.UserDetailsResponse
import com.bankprotection.authentication.domain.repositories.IRemoteUserDetailsDataSource
import retrofit2.Response
import retrofit2.http.GET

interface RemoteUserDetailsDataSource : IRemoteUserDetailsDataSource {
    @GET("/api/me/user/")
    override suspend fun getUserDetails(): Response<UserDetailsResponse>
}