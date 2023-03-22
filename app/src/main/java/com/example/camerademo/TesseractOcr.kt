package com.example.camerademo

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI

class TesseractOcr(context : Context) {

    private val tessBaseApi = TessBaseAPI()
    private val path= context.getExternalFilesDir(null).toString()+"/tessdata/hrv.traineddata"

    fun init() {
        tessBaseApi.init(path, "hrv")
    }

    fun getOCRResult(bitmap: Bitmap): String {
        tessBaseApi.setImage(bitmap)
        return tessBaseApi.utF8Text
    }

    fun stop() {
        tessBaseApi.end()
    }
}