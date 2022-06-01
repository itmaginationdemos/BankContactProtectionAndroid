package com.bankprotection.verification.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.bankprotection.application.R
import com.bankprotection.common.IDispatchers
import com.bankprotection.common.data.NoConnectionNetworkException
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.common.domain.getResultOrNull
import com.bankprotection.common.domain.repository.ISystemResourcesRepository
import com.bankprotection.utils.ui.DefaultViewEffect
import com.bankprotection.utils.ui.StateViewModel
import com.bankprotection.verification.domain.model.VerificationRequestResponse
import com.bankprotection.verification.domain.model.VerificationStatus
import com.bankprotection.verification.domain.usecase.GetPendingVerificationUseCase
import com.bankprotection.verification.domain.usecase.RequestVerificationUseCase
import com.bankprotection.verification.domain.usecase.SetVerificationVerifiedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    dispatchers: IDispatchers,
    private val systemResources: ISystemResourcesRepository,
    private val requestVerificationUseCase: RequestVerificationUseCase,
    private val getPendingVerificationUseCase: GetPendingVerificationUseCase,
    private val setVerificationVerifiedUseCase: SetVerificationVerifiedUseCase
) : StateViewModel<VerificationState, VerificationIntent, DefaultViewEffect>(dispatchers) {

    private var refreshPendingVerificationJob: Job? = null
    private var userIsStaff = false
    private var verificationRequestResponse: VerificationRequestResponse? = null

    override suspend fun onIntent(intent: VerificationIntent): VerificationState? {
        return when (intent) {
            is VerificationIntent.InitVerificationIntent -> onInitVerificationIntent(intent)
            is VerificationIntent.RequestVerificationIntent -> onRequestVerification(intent)
            is VerificationIntent.GetPendingVerification -> onGetPendingVerification(intent)
            is VerificationIntent.StartPendingVerificationRefresh -> onStartPendingVerificationRefresh()
            is VerificationIntent.StopPendingVerificationRefresh -> onStopPendingVerificationRefresh()
            is VerificationIntent.ConfirmValidGivenContactCode -> onConfirmValidGivenContactCode()
        }
    }

    private suspend fun onInitVerificationIntent(
        intent: VerificationIntent.InitVerificationIntent
    ): VerificationState? {
        intent.getUserResult
            .collect { loadingResult ->
                when (loadingResult) {
                    LoadingResult.Loading -> setState(VerificationState.LoadingVerificationState)
                    is LoadingResult.Result -> {
                        loadingResult.getResultOrNull()?.let { result ->
                            result.onSuccess { userUiModel ->
                                userIsStaff = userUiModel.isStaff
                                setState(
                                    VerificationState.InitSuccessVerificationState(
                                        isStaff = userUiModel.isStaff
                                    )
                                )
                            }
                            result.onFailure { error ->
                                setState(
                                    when (error) {
                                        is NoConnectionNetworkException ->
                                            VerificationState.ErrorVerificationState(
                                                throwable = error,
                                                message = systemResources.getString(R.string.no_internet_connection)
                                            )
                                        else ->
                                            VerificationState.ErrorVerificationState(
                                                throwable = error,
                                                message = systemResources.getString(R.string.unknown_error_message)
                                            )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        return null
    }

    private suspend fun onRequestVerification(
        intent: VerificationIntent.RequestVerificationIntent
    ): VerificationState? {
        if (intent.phoneNumberBase.isBlank() || intent.prefix.isBlank()) {
            return VerificationState.RequestVerificationFailedState(
                message = systemResources.getString(R.string.please_provide_correct_phone_number)
            )
        }

        requestVerificationUseCase.requestVerification(
            phoneNumberBase = intent.phoneNumberBase,
            prefix = intent.prefix
        ).collect { loadingResult ->
            when (loadingResult) {
                LoadingResult.Loading -> setState(VerificationState.LoadingVerificationState)
                is LoadingResult.Result -> {
                    loadingResult.getResultOrNull()?.let { result ->
                        result.onSuccess {
                            verificationRequestResponse = it
                            setState(
                                VerificationState.PendingGiverVerificationState(
                                    isStaff = userIsStaff,
                                    givenVerificationCode = it.given_verification_code
                                )
                            )
                        }
                        result.onFailure {
                            verificationRequestResponse = null
                            setState(
                                VerificationState.RequestVerificationFailedState(
                                    message = null,
                                    error = it
                                )
                            )
                        }
                    }
                }
            }
        }
        return null
    }

    private suspend fun onGetPendingVerification(
        intent: VerificationIntent.GetPendingVerification
    ): VerificationState? {
        getPendingVerificationUseCase.getPendingVerification().collect { loadingResult ->
            when (loadingResult) {
                LoadingResult.Loading -> {
                    // Do nothing
                }
                is LoadingResult.Result -> {
                    loadingResult.getResultOrNull()?.let { result ->
                        result.onSuccess { verificationRequestResponse ->
                            this.verificationRequestResponse = verificationRequestResponse
                            if (verificationRequestResponse == null) {
                                VerificationState.NoPendingVerificationState
                            } else {
                                when (verificationRequestResponse.status) {
                                    VerificationStatus.REQUESTED -> VerificationState.PendingGiverVerificationState(
                                        isStaff = userIsStaff,
                                        givenVerificationCode = verificationRequestResponse.given_verification_code
                                    )
                                    VerificationStatus.VERIFIED_BY_REQUESTER -> VerificationState.PendingRequesterVerificationState(
                                        isStaff = userIsStaff,
                                        requesterVerificationCode = verificationRequestResponse.requester_verification_code
                                    )
                                    VerificationStatus.VERIFIED_BY_GIVEN -> VerificationState.VerificationSuccessVerificationState(
                                        isStaff = userIsStaff
                                    )
                                }
                            }.let { newState ->
                                setState(newState)
                            }
                        }
                        result.onFailure {
                            setState(
                                VerificationState.PendingVerificationFailedState(
                                    error = it
                                )
                            )
                        }
                    }
                }
            }
        }
        return null
    }

    private suspend fun onStartPendingVerificationRefresh(): VerificationState? {
        if (refreshPendingVerificationJob == null) {
            refreshPendingVerificationJob = viewModelScope.launch {
                while (isActive) {
                    delay(PENDING_VERIFICATION_REFRESH_INTERVAL_MS)
                    emitIntent(VerificationIntent.GetPendingVerification)
                }
            }
        }
        return null
    }

    private fun onStopPendingVerificationRefresh(): VerificationState? {
        stopPendingVerificationRefresh()
        return null
    }

    private suspend fun onConfirmValidGivenContactCode(): VerificationState? {
        verificationRequestResponse?.let {
            setVerificationVerifiedUseCase.setVerificationVerified(
                verificationRequestId = it.id,
                isStaff = userIsStaff
            ).collect { loadingResult ->
                when (loadingResult) {
                    LoadingResult.Loading -> {
                        setState(VerificationState.LoadingVerificationState)
                    }
                    is LoadingResult.Result -> {
                        loadingResult.getResultOrNull()?.let { result ->
                            result.onSuccess {
                                emitIntent(
                                    VerificationIntent.StartPendingVerificationRefresh
                                )
                            }
                            result.onFailure { error ->
                                setState(VerificationState.ErrorVerificationState(error))
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        stopPendingVerificationRefresh()
    }

    private fun stopPendingVerificationRefresh() {
        refreshPendingVerificationJob?.let { job ->
            job.cancel()
            refreshPendingVerificationJob = null
        }
    }

    override fun getDefaultState(): VerificationState =
        VerificationState.IdleVerificationState

    companion object {
        private const val PENDING_VERIFICATION_REFRESH_INTERVAL_MS = 2000L
    }
}