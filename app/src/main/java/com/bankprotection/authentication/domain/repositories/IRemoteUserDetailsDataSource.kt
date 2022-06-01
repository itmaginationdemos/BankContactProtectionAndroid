package com.bankprotection.authentication.domain.repositories

import com.bankprotection.authentication.domain.models.UserDetailsResponse
import retrofit2.Response

interface IRemoteUserDetailsDataSource {
    suspend fun getUserDetails(): Response<UserDetailsResponse>
}