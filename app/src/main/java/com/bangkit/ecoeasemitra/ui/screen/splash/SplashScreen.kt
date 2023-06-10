package com.bangkit.ecoeasemitra.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
){
    val backgroundColor = Color(0xff1DD297)
    Box(
        modifier = modifier
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
            .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ecoease_icon),
                contentDescription = "app icon",
                modifier = Modifier
                    .size(124.dp)
            )
            Text(
                text = "EcoEase",
                style = MaterialTheme.typography.h3.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            )
        }
    }
}
//Light Mode
@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun SplashScreenPreview(){
    EcoEaseTheme {
        Surface(
            color = MaterialTheme.colors.surface
        ) {
            SplashScreen()
        }
    }
}

//Dark Mode
@Preview(showBackground = true, device = Devices.PIXEL_4, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SplashScreenPreviewDark(){
    EcoEaseTheme {
        Surface(
            color = MaterialTheme.colors.surface
        ) {
            SplashScreen()
        }
    }
}