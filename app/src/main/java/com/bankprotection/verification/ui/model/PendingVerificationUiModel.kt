package com.bankprotection.verification.ui.model

import com.bankprotection.verification.domain.model.VerificationRequestResponse

sealed interface PendingVerificationUiModel
object NoPendingVerificationUiModel : PendingVerificationUiModel
data class PendingVerificationSuccessUiModel(
    val requester_verification_code: String,
    val given_verification_code: String
) : PendingVerificationUiModel

fun VerificationRequestResponse.toPendingVerificationUiModel() = PendingVerificationSuccessUiModel(
    requester_verification_code = this.requester_verification_code,
    given_verification_code = this.given_verification_code
)

