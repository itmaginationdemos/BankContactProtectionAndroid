package com.bankprotection.tests_common

import com.bankprotection.common.IDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@ExperimentalCoroutinesApi
class TestDispatchers: IDispatchers {
    override val Main: CoroutineDispatcher
        get() = StandardTestDispatcher()
    override val Default: CoroutineDispatcher
        get() =  StandardTestDispatcher()
    override val IO: CoroutineDispatcher
        get() =  StandardTestDispatcher()
    override val Unconfined: CoroutineDispatcher
        get() =  UnconfinedTestDispatcher()
}