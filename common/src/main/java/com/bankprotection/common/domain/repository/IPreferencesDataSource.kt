package com.bankprotection.common.domain.repository

interface IPreferencesDataSource {
    fun putString(key: String, value: String?): IPreferencesDataSource
    fun getString(key: String, defValue: String?): String?
    fun getNotEmptyStringOrNull(key: String): String?
    fun remove(key: String): IPreferencesDataSource
    fun contains(key: String): Boolean
}