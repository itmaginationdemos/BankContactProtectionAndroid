package com.bankprotection.application

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bankprotection.common.domain.LoadingProgressResult
import com.bankprotection.common.domain.LoadingResult
import com.bankprotection.tests_common.MainCoroutineRule
import com.bankprotection.tests_common.captureValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ExampleViewModelFlowTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    val testSuccessResult = LoadingResult.Result(Result.success(2))

    private inner class TestRepository {
        fun singleEventFlow() = flow {
            emit(LoadingResult.Loading)
            delay(1000)
            emit(testSuccessResult)
        }

        fun multipleEventFlow() = flow {
            for (progress in 0..100 step 10) {
                delay(1000)
                emit(LoadingProgressResult.Loading(progress))
            }
            emit(testSuccessResult)
        }
    }

    private inner class TestViewModel(
        testRepository: TestRepository
    ) : ViewModel() {
        val singleEventFlowLiveData = testRepository.singleEventFlow().asLiveData()
        val multipleEventFlowLiveData = testRepository.multipleEventFlow().asLiveData()
    }

    @Test
    fun `TestRepository given, when collect single event, then get correct value`() = runTest {
        val testRepository = TestRepository()
        val results = testRepository.singleEventFlow().toList()
        assertEquals(results.size, 2)
        assertEquals(LoadingResult.Loading, results[0])
        assertEquals(testSuccessResult, results[1])
    }

    @Test
    fun `TestRepository given, when collect multiple events, then get correct values`() = runTest {
        val testRepository = TestRepository()
        val results = testRepository.multipleEventFlow().toList()
        assertEquals(results.size, 12)
        for ((index, progress) in (0..100 step 10).withIndex()) {
            assertEquals(progress, (results[index] as LoadingProgressResult.Loading).progress)
        }
        assertEquals(testSuccessResult, results[11])
    }

    @Test
    fun `TestViewModel with TestRepository given, when collect single event live data, then get correct value`() = runTest {
        val testRepository = TestRepository()
        val testViewModelTest = TestViewModel(testRepository)

        testViewModelTest.singleEventFlowLiveData.captureValues {
            advanceUntilIdle()
            assertEquals(
                arrayListOf(
                    LoadingResult.Loading,
                    testSuccessResult
                ),
                values
            )
        }
    }

    @Test
    fun `TestViewModel with TestRepository given, when collect multiple events, then get correct values`() = runTest {
        val testRepository = TestRepository()
        val testViewModelTest = TestViewModel(testRepository)

        testViewModelTest.multipleEventFlowLiveData.captureValues {
            advanceUntilIdle()
            for ((index, progress) in (0..100 step 10).withIndex()) {
                assertEquals(LoadingProgressResult.Loading(progress), values[index])
            }
            assertEquals(testSuccessResult, values[11])
        }
    }
}