package com.mss.mssproject.extension

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val formatter = DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.KOREAN))

fun BigDecimal.toFormattedString(): String = formatter.format(this)
