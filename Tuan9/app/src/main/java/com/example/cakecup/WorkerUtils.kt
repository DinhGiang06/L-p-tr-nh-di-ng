package com.example.cakecup

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun blurBitmap(bitmap: Bitmap, applicationContext: Context): Bitmap {
    var rsContext: RenderScript? = null
    try {
        // Sửa lỗi: bitmap.config có thể null, ta dùng ARGB_8888 làm mặc định
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, config)
        
        rsContext = RenderScript.create(applicationContext)
        val inAlloc = Allocation.createFromBitmap(rsContext, bitmap)
        val outAlloc = Allocation.createTyped(rsContext, inAlloc.type)
        val theIntrinsic = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))

        theIntrinsic.apply {
            setRadius(10f)
            setInput(inAlloc)
            forEach(outAlloc)
        }
        outAlloc.copyTo(output)
        return output
    } finally {
        rsContext?.destroy()
    }
}

fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
    val name = String.format("blur-filter-output-%s.png", UUID.randomUUID().toString())
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (ignore: Exception) {
            }
        }
    }
    return Uri.fromFile(outputFile)
}