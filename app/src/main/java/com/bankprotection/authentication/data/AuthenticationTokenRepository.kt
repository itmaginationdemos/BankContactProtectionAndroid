package com.bankprotection.authentication.data

import com.bankprotection.authentication.domain.repositories.IAuthenticationTokenRepository
import javax.inject.Inject

class AuthenticationTokenRepository @Inject constructor(): IAuthenticationTokenRepository {
    override fun setAuthenticationToken(token: String?) {
        TODO("Not yet implemented")
    }

    override fun getAuthenticationToken(): String? {
        TODO("Not yet implemented")
    }

    override fun setRefreshToken(token: String?) {
        TODO("Not yet implemented")
    }

    override fun getRefreshToken(): String? {
        TODO("Not yet implemented")
    }
}