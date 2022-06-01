package com.bankprotection.common.domain

import com.bankprotection.common.IDispatchers

/**
 * Abstract UseCase class.
 *
 * Extend this class to implement UseCase.
 */
abstract class UseCase(
    val dispatchers: IDispatchers
)