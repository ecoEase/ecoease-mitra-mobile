package com.bangkit.ecoeasemitra.ui.screen

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bangkit.ecoeasemitra.ui.component.FloatingButton
import com.bangkit.ecoeasemitra.ui.theme.EcoEaseTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionsRequired
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.*
import java.util.concurrent.Executor
import com.bangkit.ecoeasemitra.helper.createImageCaptureUseCase
import com.bangkit.ecoeasemitra.helper.getOutputDirectory
import com.bangkit.ecoeasemitra.helper.takePhoto
import kotlinx.coroutines.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavHostController,
    executor: Executor
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) else listOf(
            Manifest.permission.CAMERA,
        )
    )

    Log.d("TAG", "CameraScreen: ${Build.VERSION.SDK_INT}")

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit){
        permissionsState.launchMultiplePermissionRequest()
    }

    DisposableEffect(Unit){
//        (executor as ExecutorService).shutdown()
        onDispose {  }
    }

    PermissionsRequired(
        multiplePermissionsState = permissionsState,
        permissionsNotGrantedContent = {
            Log.d("TAG", "CameraScreen: permission error not not granted")
        },
        permissionsNotAvailableContent = {
            Log.d("TAG", "CameraScreen: permission error not available")
        }) {
        CameraScreenContent(context = context, lifecycleOwner = lifecycleOwner, executor = executor, navController = navController)
    }
}

@Composable
fun CameraScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    executor: Executor,
) {
    val previewView = remember { PreviewView(context) }
    var cameraSelector: CameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(previewView){
        imageCapture = context.createImageCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector,
            previewView = previewView,
        )
    }

    Box(modifier = modifier.fillMaxSize()){
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        FloatingButton(
            description = "take picture",
            icon = Icons.Default.CameraAlt,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp)
                .size(64.dp),
            onClick = {
                takePhoto(
                    context = context,
                    filename = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture!!,
                    outpuDirectory = getOutputDirectory(context),
                    executor = executor,
                    onImageCapture = {
                        Log.d("Camera", "CameraScreenContent: $it")
                        val imageUri = it.toString()
                        Log.d("Camera", "CameraScreenContent: $it")

                        CoroutineScope(Dispatchers.Main).launch {
//                            navController.navigate(Screen.Scan.setImage(
//                                URLEncoder.encode(imageUri, StandardCharsets.UTF_8.toString())
//                            ))
                        }
                    },
                    onError = { Log.d("Camera", "CameraScreenContent: $it") }
                )
            }
        )
        FloatingButton(
            description = "gallery",
            icon = Icons.Default.BrowseGallery,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = (-32).dp)
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewCameraScreen() {
    EcoEaseTheme {
        CameraScreen(
            navController = rememberNavController(),
            executor = ContextCompat.getMainExecutor(LocalContext.current)
        )
    }
}
