package com.bankprotection.application

import com.bankprotection.tests_common.TestDispatchers
import com.bankprotection.common.domain.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalCoroutinesApi
class ExampleUseCaseTest {
    class UserDataRepository {
        private var data: Int? = null
        suspend fun setData(newData: Int) {
            delay(1000)
            data = newData
        }

        suspend fun getData(): Int? {
            delay(1000)
            return data
        }
    }

    private class TestUseCase(
        val userDataRepository: UserDataRepository
    ) : UseCase(TestDispatchers()) {

        suspend fun setValue(newValue: Int) {
            delay(1000)
            userDataRepository.setData(newValue)
        }

        suspend fun getValue(): Result<Int> {
            delay(1000)
            val result = userDataRepository.getData()
            return if (result == null) {
                Result.failure(RuntimeException())
            } else {
                Result.success(result)
            }
        }
    }

    private val testValue = 1234

    @Test
    fun `TestUseCase given, When no value set, Then return failure result`() = runTest {
        val testRepository = UserDataRepository()
        val testUseCase = TestUseCase(testRepository)
        val result = testUseCase.getValue()
        assertTrue(result.isFailure)
    }

    @Test
    fun `TestUseCase given, When value set, Then return set value`() = runTest {
        val testRepository = UserDataRepository()
        val testUseCase = TestUseCase(testRepository)
        testUseCase.setValue(testValue)
        assertEquals(Result.success(testValue), testUseCase.getValue())
    }
}