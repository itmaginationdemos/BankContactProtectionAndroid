package com.bankprotection.authentication.domain.repositories

import com.bankprotection.authentication.domain.models.UserDetailsResponse
import com.bankprotection.utils.toThrowable
import javax.inject.Inject

interface IUserDetailsRepository {
    suspend fun getUser(): Result<UserDetailsResponse>
}

class UserDetailsRepository @Inject constructor(
    private val remoteUserDetailsDataSource: IRemoteUserDetailsDataSource
) : IUserDetailsRepository {
    override suspend fun getUser(): Result<UserDetailsResponse> {
        return remoteUserDetailsDataSource.getUserDetails().let { response ->
            response.body()?.let { userDetailsResponse ->
                Result.success(userDetailsResponse)
            } ?: Result.failure(response.toThrowable())
        }
    }
}