package com.bankprotection.verification.data.source

import com.bankprotection.verification.data.model.VerificationRequestModel
import com.bankprotection.verification.domain.model.VerificationRequestResponse
import retrofit2.Response

interface IVerificationRemoteDataSource {
    suspend fun requestVerification(
        verificationRequestModel: VerificationRequestModel
    ): Response<VerificationRequestResponse>

    suspend fun getPendingVerification(): Response<List<VerificationRequestResponse>>
    suspend fun setStatusVerifiedByRequester(id: Int): Response<Unit>
    suspend fun setStatusVerifiedByGiven(id: Int): Response<Unit>
}