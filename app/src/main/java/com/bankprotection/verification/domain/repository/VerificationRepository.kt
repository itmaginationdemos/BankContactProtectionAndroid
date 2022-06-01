package com.bankprotection.verification.domain.repository

import com.bankprotection.utils.toResult
import com.bankprotection.verification.data.model.VerificationRequestModel
import com.bankprotection.verification.data.source.IVerificationRemoteDataSource
import com.bankprotection.verification.domain.model.VerificationRequestResponse
import java.lang.RuntimeException
import javax.inject.Inject

class VerificationRepository @Inject constructor(
    private val remoteDataSource: IVerificationRemoteDataSource
) : IVerificationRepository {
    override suspend fun requestVerification(
        phoneNumberBase: String,
        prefix: String
    ): Result<VerificationRequestResponse> {
        if (phoneNumberBase.isBlank() || prefix.isBlank()) {
            Result.failure<VerificationRequestResponse>(
                RuntimeException(
                    "phoneNumberBase ($phoneNumberBase) and/or prefix ($prefix) is blank"
                )
            )
        }
        return remoteDataSource.requestVerification(
            VerificationRequestModel(
                phoneNumber = phoneNumberBase,
                prefix = prefix
            )
        ).toResult()
    }

    override suspend fun getPendingVerification(): Result<VerificationRequestResponse?> {
        val result = remoteDataSource.getPendingVerification().toResult()
        return if (result.isSuccess) {
            return Result.success(result.getOrNull()?.firstOrNull())
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    override suspend fun setStatusVerifiedByRequester(id: Int): Result<Unit> {
        return remoteDataSource.setStatusVerifiedByRequester(id).toResult()
    }

    override suspend fun setStatusVerifiedByGiven(id: Int): Result<Unit> {
        return remoteDataSource.setStatusVerifiedByGiven(id).toResult()
    }
}