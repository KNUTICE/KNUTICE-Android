package com.doyoonkim.domain.interfaces

/**
 * @author kimdoyoon
 * Created 6/17/25 at 12:40 AM
 */
interface ImageRemoteRepository {
    suspend fun getImageByteArrayFromUrl(url: String): ByteArray?
}