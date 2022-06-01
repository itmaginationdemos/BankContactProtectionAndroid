package com.bankprotection.application

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.bankprotection.tests_common.TestDispatchers
import com.bankprotection.common.IDispatchers
import com.bankprotection.common.domain.UseCase
import com.bankprotection.tests_common.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ExampleViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatchers = TestDispatchers()
    private val testValue = 2

    private class TestUseCase : UseCase(TestDispatchers()) {
        private var value = 1

        suspend fun setValue(newValue: Int) {
            delay(1000)
            value = newValue
        }

        suspend fun getValue(): Result<Int> {
            delay(1000)
            return Result.success(value)
        }
    }

    private class TestViewModel(
        val dispatchers: IDispatchers,
        val useCase: TestUseCase
    ) : ViewModel() {
        private var _userData: MutableLiveData<Int> = MutableLiveData<Int>()
        val userData: LiveData<Int> = _userData

        suspend fun saveUserData(newData: Int) {
            viewModelScope.launch(dispatchers.Default) {
                useCase.setValue(newData)
            }
        }

        suspend fun requestData() {
            viewModelScope.launch(dispatchers.Default) {
                _userData.value = useCase.getValue().getOrThrow()
            }
        }
    }

    @Test
    fun `TestViewModel given, When set user data, then return the set data`() =
        runTest {
            val testUseCase = TestUseCase()
            val testViewModel = TestViewModel(
                dispatchers = testDispatchers,
                useCase = testUseCase
            )
            testViewModel.saveUserData(testValue)
            testViewModel.requestData()
            advanceUntilIdle()
            val userData = testViewModel.userData.value
            assertEquals(testValue, userData)
        }
}