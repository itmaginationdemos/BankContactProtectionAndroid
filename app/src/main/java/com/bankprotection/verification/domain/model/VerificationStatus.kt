package com.bankprotection.verification.domain.model

import com.google.gson.annotations.SerializedName

enum class VerificationStatus {
    @SerializedName("RQ")
    REQUESTED,
    @SerializedName("VR")
    VERIFIED_BY_REQUESTER,
    @SerializedName("VG")
    VERIFIED_BY_GIVEN
}