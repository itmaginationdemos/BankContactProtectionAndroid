package com.bankprotection.verification.data.source

import com.bankprotection.verification.data.model.VerificationRequestModel
import com.bankprotection.verification.domain.model.VerificationRequestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VerificationRemoteDataSource : IVerificationRemoteDataSource {
    @POST("/api/verifications/request_verification/")
    override suspend fun requestVerification(
        @Body verificationRequestModel: VerificationRequestModel
    ): Response<VerificationRequestResponse>

    @GET("/api/verifications/")
    override suspend fun getPendingVerification(): Response<List<VerificationRequestResponse>>

    @POST("/api/verifications/{id}/set_verified_by_requester/")
    override suspend fun setStatusVerifiedByRequester(@Path("id") id: Int): Response<Unit>

    @POST("/api/verifications/{id}/set_verified_by_given/")
    override suspend fun setStatusVerifiedByGiven(@Path("id") id: Int): Response<Unit>
}