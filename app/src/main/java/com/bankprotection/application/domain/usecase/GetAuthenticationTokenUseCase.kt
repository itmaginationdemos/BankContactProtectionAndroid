package com.bankprotection.application.domain.usecase

import com.bankprotection.authentication.domain.models.AuthenticationTokenRequest
import com.bankprotection.authentication.domain.models.AuthenticationTokenResponse
import com.bankprotection.authentication.domain.repositories.AuthenticationRepository
import com.bankprotection.common.IDispatchers
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.common.domain.UseCase
import com.bankprotection.common.domain.mapResultToLoadingResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAuthenticationTokenUseCase @Inject constructor(
    dispatchers: IDispatchers,
    private val authenticationRepository: AuthenticationRepository
) : UseCase(dispatchers) {
    suspend fun getAuthenticationToken(
        username: String,
        password: String
    ): Flow<LoadingResult<AuthenticationTokenResponse>> = flow {
        emit(LoadingResult.Loading)
        emit(
            authenticationRepository.getAuthenticationToken(
                authenticationTokenRequest = AuthenticationTokenRequest(
                    username,
                    password
                )
            ).mapResultToLoadingResult()
        )
    }.flowOn(dispatchers.Default)
}