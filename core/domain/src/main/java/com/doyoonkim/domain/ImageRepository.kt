package com.doyoonkim.domain

import kotlinx.coroutines.flow.Flow

/**
 * @author kimdoyoon
 * Created 6/17/25 at 12:40 AM
 */
interface ImageRepository {
    fun getImageByteArrayFromUrl(url: String): Flow<ByteArray?>
}