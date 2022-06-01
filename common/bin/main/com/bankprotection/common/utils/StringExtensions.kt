package com.bankprotection.common.utils

fun String?.getNotEmptyOrNull(): String? {
    return takeIf { !isNullOrEmpty() }
}