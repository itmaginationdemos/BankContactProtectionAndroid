package com.bankprotection.common.domain

sealed class LoadingProgressResult<out Type> {
    data class Loading(val progress: Int) : LoadingProgressResult<Nothing>()
    data class Result<Type>(val result: kotlin.Result<Type>) : LoadingProgressResult<Type>()
}