package com.bankprotection.application

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.bankprotection.tests_common.TestDispatchers
import com.bankprotection.common.IDispatchers
import com.bankprotection.common.domain.LoadingProgressResult
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.tests_common.MainCoroutineRule
import com.bankprotection.tests_common.captureValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ExampleViewModelOneShotTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatchers = TestDispatchers()
    private val testSuccess = Result.success(2)

    private inner class TestViewModel(
        val dispatchers: IDispatchers
    ) : ViewModel() {
        val singleShotLoadingResultLiveData = liveData {
            emit(LoadingResult.Loading)
            emit(LoadingResult.Result(result = testSuccess))
        }

        val singleShotLoadingWithProgressResultLiveData = liveData(dispatchers.Default) {
            for (progress in 0..100 step 10) {
                emit(LoadingProgressResult.Loading(progress))
            }
            emit(LoadingProgressResult.Result(testSuccess))
        }
    }

    @Test
    fun `TestViewModel given, when get singleShotLoadingResultLiveData, then the live data return loading followed by result`() =
        runTest {
            val testViewModel = TestViewModel(dispatchers = testDispatchers)
            testViewModel.singleShotLoadingResultLiveData.captureValues {
                advanceUntilIdle()
                Assert.assertEquals(
                    arrayListOf(
                        LoadingResult.Loading,
                        LoadingResult.Result(testSuccess)
                    ),
                    values
                )
            }
        }

    @Test
    fun `TestViewModel given, when get singleShotLoadingWithProgressResultLiveData, then the live data return loadings with progresses followed by result`() =
        runTest {
            val testViewModel = TestViewModel(dispatchers = testDispatchers)
            testViewModel.singleShotLoadingWithProgressResultLiveData.captureValues {
                advanceUntilIdle()
                for ((index, progress) in (0..100 step 10).withIndex()) {
                    Assert.assertEquals(LoadingProgressResult.Loading(progress), values[index])
                }
                Assert.assertEquals(LoadingProgressResult.Result(testSuccess), values[11])
            }
        }
}