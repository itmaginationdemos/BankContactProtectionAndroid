package com.bankprotection.verification.data.model

data class VerificationRequestModel(
    val phone_number: String
) {
    constructor(
        phoneNumber: String,
        prefix: String
    ) : this("${phoneNumber}x${prefix}")
}