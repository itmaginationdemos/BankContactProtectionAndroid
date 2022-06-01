package com.bankprotection.verification.ui.model

import com.bankprotection.verification.domain.model.VerificationRequestResponse

data class VerificationRequestResponseUiModel(
    val requester_verification_code: String,
    val given_verification_code: String
)

fun VerificationRequestResponse.toVerificationRequestUiModel(): VerificationRequestResponseUiModel {
    return VerificationRequestResponseUiModel(
        requester_verification_code = this.requester_verification_code,
        given_verification_code = this.given_verification_code
    )
}