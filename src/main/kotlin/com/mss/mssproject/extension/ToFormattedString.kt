package com.mss.mssproject.extension

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private val formatter = DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.KOREAN))
fun BigDecimal.toFormattedString(): String {
    return formatter.format(this)
}