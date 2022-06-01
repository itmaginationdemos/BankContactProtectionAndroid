package com.bankprotection.common.domain.repository

interface ISystemResourcesRepository {
    fun getString(resId: Int): String
}