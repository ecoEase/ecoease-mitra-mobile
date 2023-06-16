package com.bangkit.ecoeasemitra.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun greeting(): String{
    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH")
    val formattedTime = currentTime.format(formatter).toInt()
    return when(formattedTime){
        in 3..11 -> "Selamat Pagi"
        in 12..14 -> "Selamat Siang"
        in 15..17 -> "Selamat Sore"
        else -> "Selamat Malam"
    }
}