package com.thiagoperea.mybills.core.extension

import java.text.DecimalFormat

fun Number.formatMonetary(): String = DecimalFormat("#,##0.00").format(this)