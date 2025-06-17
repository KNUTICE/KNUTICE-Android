package com.doyoonkim.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 6/16/25 at 11:53 PM
 */
interface BitmapHandler {
    fun decodeByteArray(byteArray: ByteArray): Bitmap
}

class BitmapHandlerImpl @Inject constructor() : BitmapHandler {
    override fun decodeByteArray(byteArray: ByteArray): Bitmap =
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}
