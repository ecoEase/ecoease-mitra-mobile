package com.bangkit.ecoeasemitra.ui.screen

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.bangkit.ecoeasemitra.R
import com.bangkit.ecoeasemitra.helper.ObjectDetection
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.data.model.ImageCaptured
import com.bangkit.ecoeasemitra.helper.getImageUriFromTempBitmap
import com.bangkit.ecoeasemitra.ui.common.UiState
import com.bangkit.ecoeasemitra.ui.component.RoundedButton
import com.bangkit.ecoeasemitra.ui.component.RoundedButtonType
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.bangkit.ecoeasemitra.ui.theme.LightGreyVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    filePath: String,
    imageCapturedState: StateFlow<UiState<ImageCaptured>>,
    onLoadingImageState: () -> Unit,
    openCamera: () -> Unit,
    openGallery: () -> Unit,
){
    val context = LocalContext.current
    var predictedImgUri: Uri? by rememberSaveable { mutableStateOf(null) }
    val defaultScanMessage = stringResource(R.string.scan_message)
    var scanMessage: String by rememberSaveable { mutableStateOf(defaultScanMessage) }
    var loadingPrediction by rememberSaveable{ mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)){
        Column(
            modifier = modifier
                .fillMaxSize()
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            imageCapturedState.collectAsState(initial = UiState.Loading).value.let { uiState ->
                Log.d("predict state", "TempScreen: $uiState")
                when(uiState){
                    is UiState.Success -> {
                        LaunchedEffect(uiState.data) {
                            loadingPrediction = true
                            predictedImgUri = getImageUriPrediction(context, uiState.data.uri, uiState.data.isBackCam)
                            loadingPrediction = false
                        }
                    }
                    is UiState.Error ->{
                        predictedImgUri = null
                    }
                    is UiState.Loading -> {
                        onLoadingImageState()
                    }
                }
            }

            if(loadingPrediction) LoadingScanAnim()
            else AsyncImage(
                model = predictedImgUri ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(394.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightGreyVariant)
                ,
                onLoading = {loadingState ->
                    Log.d("TAG", "TempScreen: loading")
                },
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.baseline_image_24),
                error = painterResource(id = R.drawable.baseline_image_24),
            )
            if(!loadingPrediction){
                if(predictedImgUri == null) Text( text = scanMessage, style = MaterialTheme.typography.caption.copy( color = DarkGrey), modifier = Modifier.padding(top = 32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 16.dp)){
                    RoundedButton( modifier = Modifier.weight(1f), text = "buka gallery", onClick = { openGallery() }, trailIcon = Icons.Default.Image, type = if(predictedImgUri != null) RoundedButtonType.SECONDARY else RoundedButtonType.PRIMARY,
                    )
                    RoundedButton(
                        modifier = Modifier.weight(1f),
                        text = if(predictedImgUri != null) stringResource(R.string.restart_scan) else stringResource(R.string.start_scan),
                        type = if(predictedImgUri != null) RoundedButtonType.SECONDARY else RoundedButtonType.PRIMARY,
                        onClick = { openCamera() },
                        trailIcon = Icons.Default.CameraAlt)
                }
            }
        }
        if(!loadingPrediction && predictedImgUri != null) RoundedButton(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            text = stringResource(id = R.string.make_report_btn),
            onClick = { navController.navigate(Screen.Order.route) })
    }
}

@Composable
fun LoadingScanAnim(
    modifier: Modifier = Modifier
){
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_image_scan_load)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        modifier = modifier
            .size(320.dp)
            .clip(RectangleShape),
        composition = composition,
        progress = { progress }
    )
}

suspend fun getImageUriPrediction(context: Context, uri: Uri, isBackCam: Boolean): Uri = withContext(Dispatchers.IO){
    // TODO: fix image prediction result, not rotated 
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        var bitmap = BitmapFactory.decodeStream(inputStream)

        val resultBitmap = ObjectDetection.run(context, bitmap, isBackCam)
        //SAVE TO GALLERY
//        return@withContext getImageUriFromBitmap(
//            context = context,
//            bitmap = resultBitmap
//        )
        //SAVE TO TEMPORARY FILE
        return@withContext getImageUriFromTempBitmap(
            context = context,
            bitmap = resultBitmap,
//            rotate = if (isBackCam) 90f else -90f
            rotate = 0f
        )
    }catch (e: Exception){
        Log.d("TAG", "TempScreen: $e")
        throw e
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewScreen(){
    EcoEaseTheme {
        ScanScreen(
            filePath = "",
            navController = rememberNavController(),
            openCamera = {},
            openGallery = {},
            onLoadingImageState = {},
            imageCapturedState = MutableStateFlow(UiState.Loading)
        )
    }
}