package com.bankprotection.utils

import com.bankprotection.authentication.data.NoConnectionErrorCode
import com.bankprotection.common.data.*
import retrofit2.Response
import kotlin.Exception


/**
 * Returns [Throwable] for an unsuccessful response.
 */
fun <T> Response<T>.toThrowable(): Throwable {
    this.errorBody()
    return when (code()) {
        401 -> UnauthorizedNetworkException(message())
        403 -> ForbiddenNetworkException(message())
        404 -> NotFoundNetworkException(message())
        NoConnectionErrorCode -> NoConnectionNetworkException(message())
        else -> OtherNetworkException(message())
    }
}

/**
 * Map [Response] to [Result].
 *
 * @return [Result] success with response body, or [Result] failure with [Throwable].
 */
fun <T> Response<T>.toResult(): Result<T> {
    return if (isSuccessful) {
        try {
            Result.success(body()!!)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    } else {
        Result.failure(toThrowable())
    }
}

/**
 * Map [Response] to [Result] with operation suspend function
 *
 * @param operation extra operation on [Result] mapped from the [Response].
 *
 * @return [Result] returned by [operation] function.
 */
suspend fun <T> Response<T>.toResult(operation: suspend (Result<T>) -> Result<T>): Result<T> {
    val result = if (isSuccessful) {
        try {
            Result.success(body()!!)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    } else {
        Result.failure(toThrowable())
    }
    return operation(result)
}