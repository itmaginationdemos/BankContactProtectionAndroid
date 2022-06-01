package com.bankprotection.common

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchers {
    val Main: CoroutineDispatcher
    val Default: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Unconfined: CoroutineDispatcher
}