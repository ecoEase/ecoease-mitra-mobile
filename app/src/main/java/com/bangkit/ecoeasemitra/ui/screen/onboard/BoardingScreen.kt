package com.bangkit.ecoeasemitra.ui.screen.onboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    val listPagerItem = listOf(
        PagerItem(R.raw.lottie_wave, "Selamat datang di EcoEase", Color(0xFFB9EDDD)),
        PagerItem(R.raw.lottie_becket_trash_can, "Tujuan kami yakni menumbuhkan kesadaran masyarakat terhadap pengelolaan sampah", Color(0xFF02AE9A)),
        PagerItem(R.raw.lottie_becket_earth_day_animation, "Ayo bantu kami untuk membuat lingkungan yang lebih baik", Color(0xFF1DD297)),
    )

    val pagerState = rememberPagerState()
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        HorizontalPager(
            pageCount = listPagerItem.size,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            state = pagerState,
        ){page ->
            LottieContent(
                lottieResId = listPagerItem[page].lottieResId,
                text = listPagerItem[page].text,
                backgroundColor = listPagerItem[page].color,
                isPlaying = pagerState.currentPage == page
            )
        }
        BoardingNavigation(
            onFinish = onFinish,
            modifier = modifier.align(Alignment.BottomCenter),
            pagerState = pagerState,
            lengthPager = listPagerItem.size,
            navController = navController
        )
    }
}

data class PagerItem(
    val lottieResId: Int,
    val text: String,
    val color: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardingNavigation(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    lengthPager: Int,
    pagerState: PagerState,
    navController: NavHostController
){
    val scope = rememberCoroutineScope()
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until lengthPager){
                    val animateDotWidth: Dp by animateDpAsState(
                        targetValue = if (pagerState.currentPage == i) 32.dp else 16.dp,
                        animationSpec = getAnimationSpec()
                    )
                    val animateColorDot by animateColorAsState(
                        targetValue = if (pagerState.currentPage == i) Color.White else Color.LightGray,
                        animationSpec = getAnimationSpec()
                    )

                    Box(modifier = modifier
                        .height(16.dp)
                        .width(animateDotWidth)
                        .clip(CircleShape)
                        .background(animateColorDot)
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(page = i)
                            }
                        }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        onFinish()
                        navController.navigate(Screen.Auth.route){
                            popUpTo(Screen.Onboard.route) {
                                inclusive = true
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color.White),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    elevation = null
                ) {
                    Text(text = stringResource(R.string.skip))
                }
                if(pagerState.currentPage == lengthPager - 1){
                    Button(
                        onClick = {
                            onFinish()
                            navController.navigate(Screen.Auth.route)
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(text = "Ayo mulai")
                    }
                }
            }
        }
    }
}

fun <T>getAnimationSpec(): TweenSpec<T>{
    return tween(
        durationMillis = 300,
        delayMillis = 0,
        easing = LinearEasing
    )
}

@Composable
fun LottieContent(
    modifier: Modifier = Modifier,
    lottieResId: Int,
    text: String,
    backgroundColor: Color,
    isPlaying: Boolean
){
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(lottieResId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )
    Column(modifier = modifier
        .fillMaxSize()
        .background(backgroundColor)
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(400.dp)
                .clip(RectangleShape),
            composition = composition,
            progress = { progress }
        )
        Text(
            text = text,
            modifier = modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h5.copy(
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewLottieScreen(){
    EcoEaseTheme {
        OnBoardingScreen(
            onFinish = {},
            navController = rememberNavController()
        )
    }
}