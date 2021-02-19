package com.thiagoperea.mybills.core.extension

fun CharSequence?.convertToDouble(): Double {
    val onlyDigits = this?.replace(Regex("[^0-9]"), "")?.toDoubleOrNull() ?: 0.0
    return onlyDigits / 100.0
}