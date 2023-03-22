package com.example.camerademo

import android.Manifest.permission.CAMERA
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import com.example.camerademo.databinding.ActivityMainBinding
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var lifecycleScope: LifecycleCoroutineScope
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var outputDirectory: File
    private lateinit var coroutineScope: CoroutineScope


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()
        lifecycleScope = lifecycle.coroutineScope
        coroutineScope = CoroutineScope(Dispatchers.IO)

        if (ContextCompat.checkSelfPermission(
                this,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }

    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Bind the camera provider to the lifecycle of the activity
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
            }
            // Set up image capture
            imageCapture = ImageCapture.Builder().build()

            // Set up image analysis
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1080, 1920))
                .build()
            imageAnalysis.setAnalyzer(
                cameraExecutor
            ) { image ->
                lifecycleScope.launch {
                    saveImageToFile(image)

                }
            }
            // Set up the camera
            cameraProvider.bindToLifecycle(
                this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture, imageAnalysis
            )
        }, ContextCompat.getMainExecutor(this))
        outputDirectory = getOutputDirectory()
    }

    private fun saveImageToFile(image: ImageProxy) {
        coroutineScope.launch {
            // Convert ImageProxy to Bitmap
            val bitmap = image.toBitmap()
            // Save Bitmap to file
            val file = File(
                outputDirectory,
                "${System.currentTimeMillis()}.jpg"
            )
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
            // Close image and bitmap
            image.close()
            bitmap?.recycle()
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider.unbindAll()
        cameraExecutor.shutdown()
        coroutineScope.cancel()
    }
    */
    }
}


