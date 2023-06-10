package com.bangkit.ecoeasemitra.ui.screen.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.ui.component.RoundedButton
import com.bangkit.ecoeasemitra.ui.component.RoundedButtonType
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.bangkit.ecoeasemitra.ui.theme.GreenPrimary

@Composable
fun SuccessScreen(
    navHostController: NavHostController,
    title: String,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_success)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GreenPrimary)
            .padding(32.dp)
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(400.dp)
                .clip(RectangleShape),
            composition = composition,
            progress = { progress }
        )
        Text(
            text = title,
            modifier = modifier.fillMaxWidth(),
            style = MaterialTheme.typography.subtitle1.copy(
                textAlign = TextAlign.Center,
                color = Color.White
            )
        )
        Box(modifier = Modifier.height(62.dp))
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RoundedButton(
                text = stringResource(R.string.home),
                type = RoundedButtonType.SECONDARY,
                onClick = {
                    navHostController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Success.route) {
                            inclusive = true
                        }
                    }
                })
            RoundedButton(text = stringResource(R.string.check_order_history), onClick = {
                navHostController.navigate(Screen.History.route) {
                    popUpTo(Screen.Success.route) {
                        inclusive = true
                    }
                }
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderScreenSuccessPreview() {
    EcoEaseTheme {
        SuccessScreen(navHostController = rememberNavController(), title = "test")
    }
}