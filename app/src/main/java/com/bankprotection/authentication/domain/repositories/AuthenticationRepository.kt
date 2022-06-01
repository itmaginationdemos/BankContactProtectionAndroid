package com.bankprotection.authentication.domain.repositories

import com.bankprotection.authentication.domain.models.AuthenticationTokenRequest
import com.bankprotection.authentication.domain.models.AuthenticationTokenResponse
import com.bankprotection.authentication.domain.models.RefreshAuthenticationTokenResponse
import com.bankprotection.utils.toThrowable
import okhttp3.MultipartBody
import javax.inject.Inject

interface IAuthenticationRepository {
    /**
     * Obtain JWT Authentication and Refresh Token,
     *
     * If [authenticationTokenRequest] provided then obtain JWT Authentication and Refresh Token
     * with Authentication backend for provided credentials. Succesfully obtained token is saved
     * in local storage.
     *
     * If [authenticationTokenRequest] is not provided then tries obtain the Authentication and
     * Refresh Token from Local Data source.
     *
     * @param authenticationTokenRequest optional authentication credentials. If provided JWT
     * Authentication and Refresh Token will be obtained with Authentication backend.
     *
     * @return [Result.success] with [AuthenticationTokenResponse] if success, [Result.failure]
     * otherwise.
     */
    suspend fun getAuthenticationToken(
        authenticationTokenRequest: AuthenticationTokenRequest? = null
    ): Result<AuthenticationTokenResponse>

    /**
     * Refresh JWT Authentication Token.
     *
     * Refresh JWT Authentication token with Authentication backend. Succesfully refreshed token
     * is saved in local storage.
     *
     * @param refresh optional refresh token, if not provided then the token will be obtained from
     * local data source.
     *
     * @return [Result.success] with [RefreshAuthenticationTokenResponse] is success,
     * [Result.failure] otherwise.
     */
    suspend fun refreshAuthenticationToken(
        refresh: String? = null
    ): Result<RefreshAuthenticationTokenResponse>
}

class AuthenticationRepository @Inject constructor(
    private val remoteDataSource: IRemoteAuthenticationDataSource,
    private val localDataSource: ILocalAuthenticationDataSource
) : IAuthenticationRepository {
    override suspend fun getAuthenticationToken(
        authenticationTokenRequest: AuthenticationTokenRequest?
    ): Result<AuthenticationTokenResponse> {
        return if (authenticationTokenRequest == null) {
            val localAuthenticationToken = localDataSource.getAuthenticationToken()
            val localRefreshToken = localDataSource.getRefreshAuthenticationToken()

            if (localAuthenticationToken.isNullOrEmpty() || localRefreshToken.isNullOrEmpty()) {
                Result.failure(RuntimeException("Missing tokens in local data source."))
            } else {
                Result.success(
                    AuthenticationTokenResponse(
                        access = localAuthenticationToken,
                        refresh = localRefreshToken
                    )
                )
            }
        } else {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", authenticationTokenRequest.username)
                .addFormDataPart("password", authenticationTokenRequest.password)
                .build()
            remoteDataSource.getAuthenticationToken(requestBody).let { response ->
                response.body()?.let { authenticationTokenResponse ->
                    with(localDataSource) {
                        setAuthenticationToken(authenticationTokenResponse.access)
                        setRefreshAuthenticationToken(authenticationTokenResponse.refresh)
                    }
                    return Result.success(authenticationTokenResponse)
                } ?: Result.failure(response.toThrowable())
            }
        }
    }

    override suspend fun refreshAuthenticationToken(
        refresh: String?
    ): Result<RefreshAuthenticationTokenResponse> {
        val refreshToken = refresh ?: localDataSource.getRefreshAuthenticationToken()
        ?: return Result.failure(RuntimeException("Missing refresh token to refresh access token."))

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("refresh", refreshToken)
            .build()
        return remoteDataSource.refreshAuthenticationToken(requestBody).let { response ->
            response.body()?.let { refreshAuthenticationTokenResponse ->
                localDataSource.setAuthenticationToken(refreshAuthenticationTokenResponse.access)
                Result.success(refreshAuthenticationTokenResponse)
            } ?: Result.failure(response.toThrowable())
        }
    }
}