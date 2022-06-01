package com.bankprotection.verification.domain.usecase

import com.bankprotection.common.IDispatchers
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.common.domain.UseCase
import com.bankprotection.common.domain.mapToLoadingResult
import com.bankprotection.verification.domain.repository.IVerificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetVerificationVerifiedUseCase @Inject constructor(
    dispatchers: IDispatchers,
    private val verificationRepository: IVerificationRepository
) : UseCase(dispatchers) {

    suspend fun setVerificationVerified(
        verificationRequestId: Int,
        isStaff: Boolean
    ): Flow<LoadingResult<Unit>> = flow {
        emit(LoadingResult.Loading)
        emit(
            if (isStaff) {
                verificationRepository.setStatusVerifiedByRequester(verificationRequestId)
            } else {
                verificationRepository.setStatusVerifiedByGiven(verificationRequestId)
            }.mapToLoadingResult()
        )
    }
}