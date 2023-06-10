package com.bangkit.ecoeasemitra.helper

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.bangkit.ecoeasemitra.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.createImageCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView
): ImageCapture {
    val preview = androidx.camera.core.Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val imageCapture = ImageCapture.Builder()
        .setTargetRotation(previewView.display.rotation)
        .build()

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageCapture
    )

    return imageCapture
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener(
            {
                continuation.resume(cameraProvider.get())
            },
            ContextCompat.getMainExecutor(this)
        )
    }
}

fun takePhoto(
    context: Context,
    filename: String,
    imageCapture: ImageCapture,
    outpuDirectory: File,
    executor: Executor,
    onImageCapture: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
){
    val photoFile = File.createTempFile(
        "temp",
        SimpleDateFormat(filename, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback{
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val uri = Uri.fromFile(photoFile)
            //save to gallery
//            val bitmap = BitmapFactory.decodeFile(uri.path)
//            saveImageToGallery(context.contentResolver, rotateBitmap(bitmap, 90f), photoFile.name)
//            Log.d("Camera", "onSuccess: $uri")

            try {
                onImageCapture(uri)
            }catch (e: Exception){
                Log.d("TAG", "onImageSaved: $e")
            }
        }

        override fun onError(exception: ImageCaptureException) {
            Log.d("Camera", "onError: $exception")
            onError(exception)
        }
    })
}

fun saveImageToGallery(contentResolver: ContentResolver, bitmap: Bitmap, displayName: String) {
    val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.WIDTH, bitmap.width)
        put(MediaStore.Images.Media.HEIGHT, bitmap.height)
    }

    val imageUri = contentResolver.insert(imageCollection, contentValues) ?: return

    contentResolver.openOutputStream(imageUri).use { outputStream ->
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
            throw IOException("Failed to save bitmap.")
        }
    }
}

fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun getOutputDirectory(context: Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}
fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
    val bytesStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesStream)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)

    bytesStream.flush()
    bytesStream.close()

    return Uri.parse(path.toString())
}
fun getImageUriFromTempBitmap(context: Context, bitmap: Bitmap, rotate: Float): Uri{
    val tempFile = File.createTempFile(
        "temp-detect-result",
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )
    val rotatedBitmap = rotateBitmap(bitmap, rotate)
    val outputStream = FileOutputStream(tempFile)
    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)


    val uri = Uri.fromFile(tempFile)

    outputStream.flush()
    outputStream.close()

    return uri
}

private val timeStamp: String = SimpleDateFormat(
    "dd-MMM-yyyy",
    Locale.US,
).format(System.currentTimeMillis())
fun createTemporaryFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}
fun Uri.toFile(context: Context): File{
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTemporaryFile(context)

    val inputStream = contentResolver.openInputStream(this) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while(inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}