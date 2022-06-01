package com.bankprotection.common.data

import java.lang.RuntimeException

sealed class NetworkException(message: String?) : RuntimeException(message)
class UnauthorizedNetworkException(message: String?) : NetworkException(message)
class ForbiddenNetworkException(message: String?) : NetworkException(message)
class NotFoundNetworkException(message: String?): NetworkException(message)
class NoConnectionNetworkException(message: String?) : NetworkException(message)
class OtherNetworkException(message: String?): NetworkException(message)