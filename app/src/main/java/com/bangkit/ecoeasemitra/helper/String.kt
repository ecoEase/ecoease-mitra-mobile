package com.bangkit.ecoeasemitra.helper

import java.text.NumberFormat
import java.util.*

fun Int.toCurrency(): String = NumberFormat.getNumberInstance(Locale.US).format(this)
fun Long.toCurrency(): String = NumberFormat.getNumberInstance(Locale.US).format(this)