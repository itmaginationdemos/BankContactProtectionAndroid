package com.bankprotection.application.ui.viewmodel

import androidx.lifecycle.*
import com.bankprotection.application.domain.usecase.GetAuthenticationTokenUseCase
import com.bankprotection.application.domain.usecase.GetUserDetailsUserCase
import com.bankprotection.authentication.domain.models.AuthenticationTokenResponse
import com.bankprotection.authentication.ui.model.UserUiModel
import com.bankprotection.authentication.ui.model.mapToUiModel
import com.bankprotection.common.IDispatchers
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.common.domain.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val dispatchers: IDispatchers,
    private val getAuthenticationTokenUseCase: GetAuthenticationTokenUseCase,
    private val getUserDetailsUserCase: GetUserDetailsUserCase
) : ViewModel() {

    fun login(
        username: String,
        password: String
    ): LiveData<LoadingResult<AuthenticationTokenResponse>> = liveData {
        emitSource(
            getAuthenticationTokenUseCase.getAuthenticationToken(
                username = username,
                password = password
            ).asLiveData()
        )
    }

    fun getUser(): Flow<LoadingResult<UserUiModel>> {
        return getUserDetailsUserCase.getUserDetails().map { loadingResult ->
            loadingResult.map {
                it.mapToUiModel()
            }
        }
    }
}