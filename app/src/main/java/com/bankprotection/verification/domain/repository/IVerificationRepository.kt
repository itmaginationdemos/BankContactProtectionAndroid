package com.bankprotection.verification.domain.repository

import com.bankprotection.verification.domain.model.VerificationRequestResponse

interface IVerificationRepository {
    suspend fun requestVerification(
        phoneNumberBase: String,
        prefix: String
    ): Result<VerificationRequestResponse>

    suspend fun getPendingVerification(): Result<VerificationRequestResponse?>

    suspend fun setStatusVerifiedByRequester(id: Int): Result<Unit>
    suspend fun setStatusVerifiedByGiven(id: Int): Result<Unit>
}