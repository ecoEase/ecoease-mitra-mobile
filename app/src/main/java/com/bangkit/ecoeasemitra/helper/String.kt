package com.bangkit.ecoeasemitra.helper

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

fun Int.toCurrency(): String = NumberFormat.getNumberInstance(Locale.US).format(this)
fun Long.toCurrency(): String = NumberFormat.getNumberInstance(Locale.US).format(this)

@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalDate(): String {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val formatter = DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm")
        val formattedDate = this.format(formatter)
        return formattedDate
    }else{
        return this
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(dateString: String): String {
    try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormatter = DateTimeFormatter.ofPattern("dd, MMMM, yyyy HH:mm")
        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        return dateTime.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        Log.d("string helper", "formatDate: ${e.message}")
    }
    return dateString
}



