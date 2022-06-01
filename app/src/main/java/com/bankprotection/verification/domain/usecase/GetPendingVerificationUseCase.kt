package com.bankprotection.verification.domain.usecase

import com.bankprotection.common.IDispatchers
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.common.domain.UseCase
import com.bankprotection.common.domain.mapToLoadingResult
import com.bankprotection.verification.domain.model.VerificationRequestResponse
import com.bankprotection.verification.domain.repository.IVerificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPendingVerificationUseCase @Inject constructor(
    dispatchers: IDispatchers,
    private val verificationRepository: IVerificationRepository
) : UseCase(dispatchers) {
    suspend fun getPendingVerification(): Flow<LoadingResult<VerificationRequestResponse?>> = flow {
        emit(LoadingResult.Loading)
        emit(verificationRepository.getPendingVerification().mapToLoadingResult())
    }
}