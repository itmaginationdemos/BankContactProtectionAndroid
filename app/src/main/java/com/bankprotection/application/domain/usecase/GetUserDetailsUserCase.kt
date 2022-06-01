package com.bankprotection.application.domain.usecase

import com.bankprotection.authentication.domain.models.UserDetailsResponse
import com.bankprotection.authentication.domain.repositories.IUserDetailsRepository
import com.bankprotection.common.IDispatchers
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.common.domain.UseCase
import com.bankprotection.common.domain.mapResultToLoadingResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserDetailsUserCase @Inject constructor(
    dispatchers: IDispatchers,
    private val userDetailsRepository: IUserDetailsRepository
) : UseCase(dispatchers) {

    fun getUserDetails(): Flow<LoadingResult<UserDetailsResponse>> = flow {
        emit(LoadingResult.Loading)
        emit(userDetailsRepository.getUser().mapResultToLoadingResult())
    }
}