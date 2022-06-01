package com.bankprotection.verification.ui.viewstate

import com.bankprotection.authentication.ui.model.UserUiModel

sealed class VerificationViewState
data class InitVerificationViewState(
    val isLoading: Boolean,
    val userUiModel: UserUiModel?,
    val isStaff: Boolean?
)