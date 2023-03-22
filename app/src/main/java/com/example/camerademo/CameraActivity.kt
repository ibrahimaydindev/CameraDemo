package com.example.camerademo

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import com.example.camerademo.databinding.ActivityCameraBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@ExperimentalGetImage
class CameraActivity : AppCompatActivity(){}
    /*
    private lateinit var cameraPreview: Preview
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: ActivityCameraBinding
    private lateinit var lifecycleScope: LifecycleCoroutineScope
    private lateinit var outputDirectory: File
    var context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope = lifecycle.coroutineScope
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraPreview = Preview.Builder()
            .build()

        if ((ContextCompat.checkSelfPermission(
                this, (
                        Manifest.permission.CAMERA)
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                this, (
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            startCamera()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSIONS
            )
        }

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }

            val streamConfigMap = cameraManager.getCameraCharacteristics(cameraId!!)
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val outputSizes = streamConfigMap?.getOutputSizes(SurfaceTexture::class.java)
            val largestSize = outputSizes?.maxByOrNull { it.width * it.height }


            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetRotation(Surface.ROTATION_90)
                .setTargetResolution(largestSize!!)
                .build()
                .also {
                    lifecycleScope.launch(Dispatchers.Main) {
                        it.setAnalyzer(cameraExecutor, TextAnalyzer())
                    }
                }
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Hata", exc)
            }
        }, ContextCompat.getMainExecutor(this))
        outputDirectory = getOutputDirectory()
    }

    @ExperimentalGetImage
    inner class TextAnalyzer : ImageAnalysis.Analyzer {

        private val recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        @OptIn(DelicateCoroutinesApi::class)
        @RequiresApi(Build.VERSION_CODES.O)
        override fun analyze(image: ImageProxy) {
            GlobalScope.launch(Dispatchers.Default) {
                val mediaImage = image.image ?: return@launch
                recognizer.process(
                    InputImage.fromMediaImage(
                        mediaImage,
                        image.imageInfo.rotationDegrees,
                    )
                )
                    .addOnSuccessListener { visionText ->
                        for (block in visionText.textBlocks) {
                            for (line in block.lines) {
                                val boundingBox = line.boundingBox
                                if ((boundingBox != null) && (boundingBox.left >= 0) && (boundingBox.top >= 0) && (boundingBox.right <= image.width) && (boundingBox.bottom <= image.height)) {
                                    val croppedBitmap = (image.toBitmapGoogle())?.let {
                                        Bitmap.createBitmap(
                                            it,
                                            boundingBox.left - 5,
                                            boundingBox.top - 3,
                                            boundingBox.width() + 20,
                                            boundingBox.height() + 5
                                        )
                                    }
                                    lifecycleScope.launch(Dispatchers.IO) {

                                            val filename =
                                                "text_element_${System.currentTimeMillis()}.jpg"
                                            val file = File(
                                                this@CameraActivity.getExternalFilesDir(null),
                                                filename
                                            )
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                val outputStream = FileOutputStream(file)
                                                croppedBitmap!!.compress(
                                                    Bitmap.CompressFormat.JPEG,
                                                    100,
                                                    outputStream
                                                )
                                                outputStream.flush()
                                                outputStream.close()

                                            }
                                        }
                                    }
                                }
                            }
                        }
                            .addOnFailureListener { e ->
                                Log.d("result", e.toString())
                            }
                            .addOnCompleteListener {
                                image.close()
                            }
                    }
            }
        }

        private fun getOutputDirectory(): File {
            val mediaDir = externalMediaDirs.firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else filesDir
        }

        @ExperimentalGetImage
        fun ImageProxy.toBitmapGoogle(): Bitmap? {

            image?.let {
                val frameMetadata: FrameMetadata = FrameMetadata.Builder()
                    .setWidth(it.width)
                    .setHeight(it.height)
                    .setRotation(imageInfo.rotationDegrees)
                    .build()
                val nv21Buffer = BitmapUtils.yuv420ThreePlanesToNV21(
                    it.planes, it.width, it.height
                )
                return BitmapUtils.getBitmap(nv21Buffer, frameMetadata)
            }
            return null
        }

        private fun permissionGranted() = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }

        companion object {
            const val REQUEST_CODE_PERMISSIONS = 10
            val REQUIRED_PERMISSIONS =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>, grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_CODE_PERMISSIONS) {
                if (permissionGranted()) {
                    startCamera()
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            cameraExecutor.shutdown()
            lifecycleScope.cancel()
        }

        private fun saveCroppedBitmapToStorage(bitmap: Bitmap) {
            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp.jpg"

            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)

            try {
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

*/