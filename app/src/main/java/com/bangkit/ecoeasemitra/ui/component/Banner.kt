package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.bangkit.ecoeasemitra.ui.theme.GreenPrimary
import com.bangkit.ecoeasemitra.ui.theme.GreenSecondary
import com.bangkit.ecoeasemitra.R

val listStringId = listOf(
    R.string.banner_text_1,
    R.string.banner_text_2,
)

@Composable
fun Banner(
    bannerAction: () -> Unit = {},
    modifier: Modifier = Modifier
){
    val density = LocalDensity.current
    val infiniteAnimation = rememberInfiniteTransition()
    var animatedText = infiniteAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = modifier
            .height(170.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
        ,
    ) {
       Box(modifier = Modifier
           .fillMaxSize()
           .background(
               Brush.linearGradient(
                   listOf(GreenPrimary, GreenSecondary)
               )
           )
           .padding(horizontal =  24.dp, vertical = 34.dp)
       ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .width(152.dp)
                        .fillMaxHeight()
                ) {
                    Crossfade(targetState = animatedText) {
                        when{
                            it.value < 2f -> {
                                AnimatedVisibility(
                                    visible = it.value < 1.5f,
                                    enter = slideInVertically {
                                        with(density){ -48.dp.roundToPx() }
                                    } + fadeIn(),
                                    exit = slideOutVertically{
                                        with(density){ 48.dp.roundToPx() }
                                    } + fadeOut()
                                ) {
                                    Text(
                                        text = stringResource(id = listStringId[0]),
                                        style = MaterialTheme.typography.subtitle1.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            it.value > 2f -> {
                                AnimatedVisibility(
                                    visible = it.value > 2.5f,
                                    enter = slideInVertically {
                                        with(density){ -100.dp.roundToPx() }
                                    } + fadeIn(),
                                    exit = slideOutVertically{
                                        with(density){ 48.dp.roundToPx() }
                                    }  + fadeOut()
                                ) {
                                    Text(
                                        text = stringResource(id = listStringId[1]),
                                        style = MaterialTheme.typography.subtitle1.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                    RoundedButton(
                        text = stringResource(R.string.take_order),
                        type = RoundedButtonType.SECONDARY,
                        onClick = bannerAction
                    )
                }
               Image(
                   painter = painterResource(id = R.drawable.people_reporting),
                   contentDescription = "banner image",
                   modifier = Modifier.weight(1f)
               )
            }
       }
    }
}

@Preview(showBackground = true)
@Composable
fun BannerPreview(){
    EcoEaseTheme {
        Banner()
    }
}