package com.bankprotection.authentication.data

import com.bankprotection.authentication.domain.repositories.AuthenticationRepository
import com.bankprotection.common.data.NoConnectionNetworkException
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

const val NoConnectionErrorCode = 901

class AuthInterceptor @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            getAuthenticatedRequest(chain.request())?.let { authenticatedRequest ->
                var response = chain.proceed(authenticatedRequest)
                // If response code 401, then 'invalid_token' - expired, revoked, malformed, etc.
                // Try refresh token and retry proceed request.
                //
                // In the final version use okhttp3.Authenticator.
                if (response.code == 401) {
                    if (refreshToken() != null) {
                        getAuthenticatedRequest(chain.request())?.let {
                            response = chain.proceed(authenticatedRequest)
                        }
                    }
                }
                return response
            } ?: chain.proceed(chain.request())
        } catch (e: Exception) {
            val msg = when (e) {
                is SocketTimeoutException -> {
                    "Timeout - Please check your internet connection"
                }
                is UnknownHostException -> {
                    "Unable to make a connection. Please check your internet"
                }
                is ConnectionShutdownException -> {
                    "Connection shutdown. Please check your internet"
                }
                is IOException -> {
                    "Server is unreachable, please try again later."
                }
                is IllegalStateException -> {
                    "${e.message}"
                }
                else -> {
                    "${e.message}"
                }
            }
            return Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(NoConnectionErrorCode)
                .message(msg)
                .body("{${e}}".toResponseBody(null)).build()
        }
    }

    private fun getAccessToken(): String? {
        return runBlocking {
            return@runBlocking authenticationRepository.getAuthenticationToken(null)
                .getOrNull()?.access
        }
    }

    private fun refreshToken(): String? {
        return runBlocking {
            authenticationRepository.refreshAuthenticationToken().getOrNull()?.access
        }
    }

    private fun getAuthenticatedRequest(request: Request): Request? {
        return getAccessToken()?.let { accessToken ->
            request.newBuilder().addHeader(
                "Authorization",
                "Bearer $accessToken"
            ).build()
        }
    }
}