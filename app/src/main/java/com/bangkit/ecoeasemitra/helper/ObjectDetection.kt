package com.bangkit.ecoeasemitra.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import com.bangkit.ecoeasemitra.ml.SsdliteMobilenetV2
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException


object ObjectDetection{


    private val label = listOf("Got Masked", "No Mask", "Wear Incorrectly")
    fun run(context: Context, image: Bitmap, isBackCam: Boolean): Bitmap{
        val model = SsdliteMobilenetV2.newInstance(context)
        // Rotate image base on camera facing
        val rotatedBitmap = rotateBitmap(image, if (isBackCam) 90f else -90f)
        // Create inputBuffer
        val scalledBitmap = Bitmap.createScaledBitmap(image, 300, 300, true)
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(scalledBitmap)
        val inputBuffer = tensorImage.buffer

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(inputBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        val outputFeature1 = outputs.outputFeature1AsTensorBuffer
        val outputFeature2 = outputs.outputFeature2AsTensorBuffer
        val outputFeature3 = outputs.outputFeature3AsTensorBuffer

        val boundLocation = outputFeature0.floatArray
        val classType = outputFeature1.intArray
        val score = outputFeature2.floatArray
        val num = outputFeature3.intArray

        val imageWithBound = drawRectBound(
            image = image,
            bounds = boundLocation,
            classType = classType[0],
            score = score[0],
            rotation = if (isBackCam) 90f else -90f
        )
        // Releases model resources if no longer used.
        model.close()
        return imageWithBound
    }
    fun convertToGrayscale(inputBitmap: Bitmap): Bitmap {
        val width = inputBitmap.width
        val height = inputBitmap.height

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = inputBitmap.getPixel(x, y)

                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val grayscale = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

                val grayPixel = Color.rgb(grayscale, grayscale, grayscale)

                outputBitmap.setPixel(x, y, grayPixel)
            }
        }

        return outputBitmap
    }
    private fun drawRectBound(image: Bitmap, bounds: FloatArray, classType: Int, score: Float, rotation: Float = 0f): Bitmap{

        var copyImage = image.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(copyImage)
        val paint = Paint()
        var colors = listOf(
            Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
            Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)

        val width = copyImage.width
        val height = copyImage.height
        val x = 0

        paint.color = colors[1]
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawRect(
            RectF(
                bounds[x+1] * width,
                bounds[x] * height,
                bounds[x+3] * width,
                bounds[x+2] * height),
            paint)//draw box
        paint.style = Paint.Style.FILL
        paint.textSize = 64f
        canvas.drawText("${label[classType]} ${score * 100}", bounds[x+1] * width, bounds[x] * height - 32, paint)//draw label and score

        return copyImage
    }
    @Throws(IOException::class)
    fun readBytes(context: Context, uri: Uri): ByteArray? =
        context.contentResolver.openInputStream(uri)?.buffered()?.use { it.readBytes() }
}
