package com.bankprotection.verification.ui.viewmodel

import com.bankprotection.authentication.ui.model.UserUiModel
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.utils.ui.IViewEffect
import com.bankprotection.utils.ui.IViewIntent
import com.bankprotection.utils.ui.IViewState
import kotlinx.coroutines.flow.Flow

sealed class VerificationIntent : IViewIntent {
    data class InitVerificationIntent(
        val getUserResult: Flow<LoadingResult<UserUiModel>>
    ) : VerificationIntent()

    data class RequestVerificationIntent(
        val phoneNumberBase: String,
        val prefix: String
    ) : VerificationIntent()

    object GetPendingVerification : VerificationIntent()
    object StartPendingVerificationRefresh : VerificationIntent()
    object StopPendingVerificationRefresh : VerificationIntent()
    object ConfirmValidGivenContactCode : VerificationIntent()
}

sealed class VerificationState : IViewState {
    object IdleVerificationState : VerificationState()
    object LoadingVerificationState : VerificationState()
    data class InitSuccessVerificationState(
        val isStaff: Boolean
    ) : VerificationState()

    data class RequestVerificationFailedState(
        val message: String?,
        val error: Throwable? = null
    ) : VerificationState()

    object LoadingPendingVerificationState : VerificationState()
    data class PendingGiverVerificationState(
        val isStaff: Boolean,
        val givenVerificationCode: String
    ) : VerificationState()

    data class PendingRequesterVerificationState(
        val isStaff: Boolean,
        val requesterVerificationCode: String
    ) : VerificationState()

    data class VerificationSuccessVerificationState(
        val isStaff: Boolean
    ) : VerificationState()

    object NoPendingVerificationState : VerificationState()
    data class PendingVerificationFailedState(
        val error: Throwable
    ) : VerificationState()

    data class ErrorVerificationState(val throwable: Throwable? = null, val message: String? = null) :
        VerificationState()
}