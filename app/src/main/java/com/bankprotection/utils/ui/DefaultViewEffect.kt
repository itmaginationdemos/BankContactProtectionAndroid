package com.bankprotection.utils.ui

sealed class DefaultViewEffect : IViewEffect {
    data class ErrorViewEffect(
        val error: Throwable?,
        val message: String?
    ) : DefaultViewEffect()

    data class InfoViewEffect(
        val message: String?
    ) : DefaultViewEffect()
}