package com.bankprotection.verification.domain.model

import java.util.Date

data class VerificationRequestResponse(
    val id: Int,
    val valid: Boolean,
    val requester: Int,
    val given: Int,
    val time: Date,
    val timeout: Date,
    val requester_verification_code: String,
    val given_verification_code: String,
    val status: VerificationStatus
)